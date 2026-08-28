// Se conservan los nombres usados por los onclick del HTML.
function togglePasswordVista() { Mochi.alternarClave('password', 'ojo'); }
function toggleConfirmPasswordVista() { Mochi.alternarClave('passwordConfirm', 'ojoConfirm'); }

document.getElementById('registroForm').addEventListener('submit', async function (event) {
    event.preventDefault();
    const nombre = document.getElementById('name').value.trim();
    const email = document.getElementById('email').value.trim().toLowerCase();
    const telefono = document.getElementById('phone').value.replace(/[\s()-]/g, '');
    const clave = document.getElementById('password').value;
    const confirmacion = document.getElementById('passwordConfirm').value;
    if (!this.reportValidity()) return;
    const partesNombre = nombre.split(/\s+/).filter(Boolean);
    if (partesNombre.length < 2) return alert('Escribe tu nombre y apellido.');
    if (!/^\d{10}$/.test(telefono)) return alert('El teléfono debe tener 10 dígitos.');
    if (clave.length < 8) return alert('La contraseña debe tener al menos 8 caracteres.');
    if (clave !== confirmacion) return alert('Las contraseñas no coinciden.');
    const boton = this.querySelector('button[type="submit"]');
    boton.disabled = true;
    try {
        if (!Mochi.api.activa) {
            if (Mochi.usuarios().some(usuario => usuario.email.toLowerCase() === email)) return alert('Este correo ya está registrado.');
            const sal = Array.from(crypto.getRandomValues(new Uint8Array(16)), byte => byte.toString(16).padStart(2, '0')).join('');
            const claveHash = await Mochi.derivarClave(clave, sal);
            const nuevoUsuario = { id: crypto.randomUUID(), nombre, email, telefono, sal, claveHash, fechaRegistro: new Date().toISOString() };
            if (!Mochi.guardar('usuariosRegistrados', [...Mochi.usuarios(), nuevoUsuario])) return;
            if (!Mochi.iniciarSesion(nuevoUsuario)) return;
            window.location.href = Mochi.destinoSesion();
            return;
        }
        const apellido = partesNombre.pop();
        const nombres = partesNombre.join(' ');
        await Mochi.api.post('/api/user', { nombre: nombres, apellido, correo: email, contrasenia: clave, telefono });
        const autenticacion = await Mochi.api.post('/auth/login', { correo: email, password: clave });
        if (!Mochi.iniciarSesion(autenticacion)) return;
        alert('Tu cuenta fue registrada correctamente.');
        window.location.href = Mochi.destinoSesion();
    } catch (error) {
        console.error('No se pudo completar el registro:', error);
        alert(error.message || 'No se pudo completar el registro.');
    } finally { boton.disabled = false; }
});
