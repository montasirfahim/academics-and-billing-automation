async function validateOTP(){
    const otp = document.getElementById("otp-field").value;
    console.log("otp: "  + otp);
    const statusEl = document.getElementById("status");

    if(!otp || otp.trim() === ""){
        statusEl.className = "error";
        statusEl.textContent = "Please enter OTP";
        return;
    }

    try {
        const btn = document.getElementById("otp-submit-btn");
        btn.disabled = true;
        const response = await fetch('/api/auth/verify-otp', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({otp})
        });

        btn.disabled = false;
        const data = await response.json();

        if (response.ok) {
            console.log("login success with otp verification: " + otp);
            statusEl.textContent = data.message;
            statusEl.className = data.status;
            setTimeout(() => window.location.href = '/home', 1000);
        } else {
            console.log(data.message);
            statusEl.className = data.status;
            statusEl.textContent = data.message;
        }

    } catch (err) {
        console.log(err);
        statusEl.className = "error";
    }
}
async function login(event) {
    event.preventDefault();
    const statusEl = document.getElementById("status");
    const otpField = document.getElementById("otp-field");
    otpField.value = "";
    const otp_form = document.getElementById("otp-form");
    otp_form.style.display = "none";

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    console.log(email); //debug
    console.log(password);

    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        const data = await response.json();
        console.log(data.message);
        if (response.ok) {
            console.log("login success with email: " + email);
            statusEl.textContent = data.message;
            statusEl.className = data.status;
            otp_form.style.display = "block";
            const loginForm = document.getElementById("login-form");
            loginForm.style.marginTop = "10px";
            setTimeout(() => window.location.href = '/home', 500); //otp bypassed, will fix it later
        } else {
            statusEl.className = data.status;
            statusEl.textContent = data.message;
        }
    } catch (err) {
        console.error(err);
        statusEl.textContent = "Server error. Try again later.";
        statusEl.style.backgroundColor = "#fee2e2";
        statusEl.style.color = "#991b1b";
    }
}

async function logout() {
    const response = await fetch('/api/auth/logout', { method: 'POST' });
    const data = await response.json();
    if (response.ok) {
        console.log(data.message);
        window.location.href = '/login';
    }
}
