/* Utilidades compartidas y cliente único del backend de MochiMexa. */
(() => {
    const paginas = new URL('../pages/', document.currentScript.src);
    const normalizar = valor => String(valor ?? '').normalize('NFD')
        .replace(/[\u0300-\u036f]/g, '').toLowerCase().trim();
    const escapar = valor => String(valor ?? '').replace(/[&<>"']/g, caracter =>
        ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[caracter]));

    function leer(clave, defecto, almacen = localStorage) {
        try {
            const valor = almacen.getItem(clave);
            return valor === null ? defecto : JSON.parse(valor);
        } catch {
            // No borramos datos previos si están dañados o el navegador los bloquea.
            return defecto;
        }
    }

    function guardar(clave, valor, almacen = localStorage) {
        try {
            almacen.setItem(clave, JSON.stringify(valor));
            return true;
        } catch {
            alert('No se pudo guardar en este navegador. Revisa el espacio disponible y los permisos de almacenamiento.');
            return false;
        }
    }

    function ruta(pagina, parametros = {}) {
        const destino = new URL(pagina, paginas);
        Object.entries(parametros).forEach(([clave, valor]) => destino.searchParams.set(clave, valor));
        return destino.href;
    }

    function contacto(asunto, mensaje = '', correo = '') {
        // Los datos personales van en un borrador de sesión, nunca en la URL.
        if ((mensaje || correo) && !guardar('mochiContactoBorrador', { mensaje, correo }, sessionStorage)) return;
        window.location.href = `${ruta('contactanos.html', { asunto })}#formTeam`;
    }

    function imagenSegura(valor) {
        const respaldo = new URL('../assets/imagenes/iconos/logos/LogoNegro.png', paginas).href;
        try {
            if (/^data:image\/(png|jpe?g|webp|gif);base64,/i.test(valor)) return valor;
            const url = new URL(valor || respaldo, paginas);
            return ['http:', 'https:', 'file:'].includes(url.protocol) ? url.href : respaldo;
        } catch { return respaldo; }
    }

    function alternarClave(id, iconoId) {
        const campo = document.getElementById(id);
        const icono = document.getElementById(iconoId);
        if (!campo || !icono) return;
        campo.type = campo.type === 'password' ? 'text' : 'password';
        icono.src = new URL(`../assets/imagenes/iconos/eye-${campo.type === 'password' ? 'slash-solid' : 'solid'}.png`, paginas).href;
        icono.closest('button').setAttribute('aria-label', campo.type === 'password' ? 'Mostrar contraseña' : 'Ocultar contraseña');
        icono.closest('button').setAttribute('aria-pressed', String(campo.type === 'text'));
    }

    async function derivarClave(clave, sal) {
        // PBKDF2 evita guardar contraseñas en texto plano. Es solo una demostración
        // local: la autorización real debe verificarse en el futuro backend.
        const material = await crypto.subtle.importKey('raw', new TextEncoder().encode(clave), 'PBKDF2', false, ['deriveBits']);
        const bits = await crypto.subtle.deriveBits({ name: 'PBKDF2', salt: new TextEncoder().encode(sal), iterations: 100000, hash: 'SHA-256' }, material, 256);
        return Array.from(new Uint8Array(bits), byte => byte.toString(16).padStart(2, '0')).join('');
    }

    const apiBase = (() => {
        const configurada = window.MOCHIMEXA_API_URL || document.querySelector?.('meta[name="mochimexa-api"]')?.content;
        if (configurada) return String(configurada).replace(/\/$/, '');
        return ['localhost', '127.0.0.1'].includes(location.hostname) ? 'http://localhost:8080' : location.origin;
    })();

    function auth() {
        const datos = leer('mochiAuth', null);
        if (!datos?.token || !datos?.user) return null;
        if (datos.expiresAt && Date.parse(datos.expiresAt) <= Date.now()) {
            localStorage.removeItem('mochiAuth');
            return null;
        }
        return datos;
    }

    function mapearUsuario(user) {
        if (!user) return null;
        const nombre = [user.nombre, user.apellido].filter(Boolean).join(' ').trim();
        return {
            id: user.idUsuario ?? user.id,
            nombre: nombre || user.nombre || '',
            nombres: user.nombre || '',
            apellido: user.apellido || '',
            email: user.correo || user.email || '',
            telefono: user.telefono || '',
            foto: user.foto || '',
            rol: String(user.rol || '').toUpperCase(),
            fechaRegistro: user.fechaRegistro
        };
    }

    async function apiRequest(path, options = {}) {
        if (typeof fetch !== 'function') throw new Error('Este navegador no permite comunicarse con el servidor.');
        const controller = new AbortController();
        const timeout = setTimeout(() => controller.abort(), options.timeout || 15000);
        const sesion = auth();
        const headers = { ...(options.headers || {}) };
        const isFormData = typeof FormData !== 'undefined' && options.body instanceof FormData;
        if (options.body != null && !isFormData) headers['Content-Type'] = 'application/json';
        if (sesion?.token) headers.Authorization = `Bearer ${sesion.token}`;
        try {
            const response = await fetch(`${apiBase}${path}`, {
                ...options,
                headers,
                body: options.body == null || isFormData ? options.body : JSON.stringify(options.body),
                signal: controller.signal
            });
            const contentType = response.headers?.get?.('content-type') || '';
            const payload = response.status === 204 ? null : contentType.includes('json') ? await response.json() : await response.text();
            if (!response.ok) {
                if (response.status === 401 && sesion?.token) {
                    localStorage.removeItem('mochiAuth');
                    if (typeof window.dispatchEvent === 'function') window.dispatchEvent(new Event('mochi:sesion'));
                }
                const error = new Error(payload?.message || payload?.error || `El servidor respondió ${response.status}.`);
                error.status = response.status;
                error.payload = payload;
                throw error;
            }
            return payload;
        } catch (error) {
            if (error.name === 'AbortError') throw new Error('El servidor tardó demasiado en responder. Inténtalo de nuevo.');
            if (error instanceof TypeError) throw new Error('No se pudo conectar con el servidor de MochiMexa. Verifica que el backend esté activo.');
            throw error;
        } finally { clearTimeout(timeout); }
    }

    const api = {
        base: apiBase,
        activa: typeof navigator !== 'undefined' && typeof navigator.userAgent === 'string',
        request: apiRequest,
        get: path => apiRequest(path),
        post: (path, body) => apiRequest(path, { method: 'POST', body }),
        put: (path, body) => apiRequest(path, { method: 'PUT', body }),
        patch: (path, body) => apiRequest(path, { method: 'PATCH', body }),
        delete: path => apiRequest(path, { method: 'DELETE' }),
        mapearUsuario
    };

    window.Mochi = {
        leer, guardar, ruta, contacto, normalizar, escapar, imagenSegura, alternarClave, derivarClave,
        // Completar SOLO con datos oficiales confirmados. No se inventan destinos.
        api,
        config: { apiBase, whatsapp: '', correo: '', recuperacion: '', cupones: [] },
        usuarios: () => {
            const usuarios = leer('usuariosRegistrados', []);
            return Array.isArray(usuarios) ? usuarios.filter(usuario => usuario && typeof usuario.email === 'string') : [];
        },
        usuarioActual() {
            const autenticado = auth();
            if (autenticado) return mapearUsuario(autenticado.user);
            const sesion = leer('usuarioSesion', null);
            if (!sesion?.email) return null;
            return Mochi.usuarios().find(usuario => sesion.id ? usuario.id === sesion.id : usuario.email.toLowerCase() === sesion.email.toLowerCase()) || null;
        },
        sesion() {
            const autenticado = auth();
            if (autenticado) return { ...mapearUsuario(autenticado.user), loginTime: autenticado.loginTime };
            const sesion = leer('usuarioSesion', null);
            if (!sesion || typeof sesion.email !== 'string') return null;
            const actual = Mochi.usuarioActual();
            // Leer los datos actuales evita perder la cuenta al editar su correo.
            return actual ? { id: actual.id, nombre: actual.nombre, email: actual.email, telefono: actual.telefono, foto: actual.foto || '', loginTime: sesion.loginTime } : null;
        },
        iniciarSesion(usuario) {
            if (usuario?.token && usuario?.user) {
                const expiresAt = new Date(Date.now() + Number(usuario.expiresIn || 0)).toISOString();
                const datos = { token: usuario.token, type: usuario.type || 'Bearer', expiresAt, loginTime: new Date().toISOString(), user: usuario.user };
                if (!guardar('mochiAuth', datos)) return false;
                localStorage.removeItem('usuarioSesion');
                if (typeof window.dispatchEvent === 'function') window.dispatchEvent(new Event('mochi:sesion'));
                return true;
            }
            const id = usuario.id || crypto.randomUUID();
            if (!usuario.id) {
                const usuarios = Mochi.usuarios();
                if (!guardar('usuariosRegistrados', usuarios.map(u => u.email.toLowerCase() === usuario.email.toLowerCase() ? { ...u, id } : u))) return false;
            }
            const { nombre, email, telefono } = usuario;
            return guardar('usuarioSesion', { id, nombre, email, telefono, loginTime: new Date().toISOString() });
        },
        cerrarSesion() {
            localStorage.removeItem('mochiAuth');
            localStorage.removeItem('usuarioSesion');
            localStorage.removeItem('sesionActiva');
            if (typeof window.dispatchEvent === 'function') window.dispatchEvent(new Event('mochi:sesion'));
        },
        actualizarUsuario(user) {
            const datos = auth();
            if (!datos) return false;
            return guardar('mochiAuth', { ...datos, user });
        },
        esAdmin() { return Mochi.usuarioActual()?.rol === 'ADMIN'; },
        destinoSesion(valor = new URLSearchParams(location.search).get('volver')) {
            // Solo se aceptan retornos a estas páginas propias; nunca a una URL externa.
            const defecto = ruta('perfil.html');
            try {
                const destino = new URL(valor || defecto, paginas);
                const permitidas = ['perfil.html', 'resumenPedido.html', 'producto.html'].map(p => new URL(ruta(p)).pathname);
                return destino.origin === paginas.origin && permitidas.includes(destino.pathname) ? destino.href : defecto;
            } catch { return defecto; }
        },
        pedirSesion(volver = location.href) {
            location.href = ruta('iniciaSesion.html', { volver: Mochi.destinoSesion(volver) });
        }
    };
})();
