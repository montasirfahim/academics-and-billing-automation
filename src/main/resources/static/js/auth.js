async function validateOTP(){
    const otp = document.getElementById("otp-field").value;
    console.log("otp: "  + otp);
    const statusEl = document.getElementById("status");

    if(!otp || otp.trim() === ""){
        statusEl.className = "error";
        statusEl.textContent = "Please enter a valid OTP";
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
    const loginBtn = document.getElementById("login-btn");
    loginBtn.innerText = "Logging in...";
    loginBtn.disabled = true;

    const statusEl = document.getElementById("status");
    const otpField = document.getElementById("otp-field");
    otpField.value = "";
    const otp_form = document.getElementById("otp-form");
    otp_form.style.display = "none";

    const forgotDiv = document.getElementById("forgot-div");
    forgotDiv.style.display = "none";

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
            forgotDiv.style.display = "block";

        }
    } catch (err) {
        console.error(err);
        statusEl.textContent = "Server error. Try again later.";
        statusEl.style.backgroundColor = "#fee2e2";
        statusEl.style.color = "#991b1b";
    } finally {
        loginBtn.innerText = "Login";
        loginBtn.disabled = false;
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

async function  resetPassword(event, userId){
    event.preventDefault();
    const currentPassword = document.getElementById("current-pass").value;
    const newPassword = document.getElementById("new-pass").value;
    const confirmedPassword = document.getElementById("confirm-pass").value;

    if(!currentPassword || !newPassword || !confirmedPassword){
        alert("Provide all required data.");
        return;
    }
    if(newPassword.length < 6){
        alert("Password must be at least 6 characters long.")
        return;
    }
    if(newPassword.includes(" ")){
        alert("Password can not contain spaces.")
        return;
    }
    if(newPassword.toString() !== confirmedPassword.toString()){
        alert("Passwords do not match.")
        return;
    }
    if(currentPassword === newPassword){
        alert("New password can not be same as your current password.");
        return;
    }
    try{
        const response = await fetch(`/api/user/reset-password`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({userId, currentPassword, newPassword})
        });
        const data = await response.json();
        if(response.ok){
            alert("Password has been updated successfully");
            window.location.href = `/dashboard`;
        }
        else{
            alert(data.message);
        }
    }catch (err){
        console.log(err);
        alert("An error occurred!");
    }
}

async function forgotPasswordForm() {
    const email = document.getElementById("email").value;
    const statusEl = document.getElementById("status");
    const forgotBtn = document.getElementById("forgot-btn");

    if (!email) {
        statusEl.textContent = "Please enter your email first.";
        statusEl.className = "error";
        return;
    }

    statusEl.textContent = "Verifying email...";
    statusEl.style.color = "#1e40af";
    forgotBtn.disabled = true;

    try {
        const response = await fetch(`/api/auth/check-email?email=${encodeURIComponent(email)}`);
        if (response.ok) {
            const userExists = await response.json();
            if (userExists) {
                statusEl.textContent = "Email verified! Redirecting...";
                statusEl.className = "success";

                setTimeout(() => {
                    window.location.href = `/reset-password-form?email=${encodeURIComponent(email)}`;
                }, 1000);
            } else {
                statusEl.textContent = "No account found with this email.";
                statusEl.className = "error";
                statusEl.style.color = "#991b1b";
                forgotBtn.disabled = false;
            }
        } else {
            throw new Error("Server responded with an error.");
        }
    } catch (error) {
        console.error(error);
        statusEl.textContent = "Something went wrong. Please try again.";
        statusEl.className = "error";
        statusEl.style.color = "#991b1b";
        forgotBtn.disabled = false;
    }
}

async function resetForgottenPassword(event){
    event.preventDefault();
    const form = document.getElementById("resetPassForm");
    const email = form.getAttribute('data-email');
    console.log(email);

    if (!email){
        alert("Invalid email address!");
        return;
    }

    const newPassword = document.getElementById("new-pass").value;
    const confirmedPassword = document.getElementById("confirm-pass").value;
    const otp = document.getElementById("otp").value;
    console.log(confirmedPassword);
    if(newPassword.length < 6){
        alert("Password must be at least 6 characters long.")
        return;
    }
    if(newPassword.includes(" ")){
        alert("Password can not contain spaces.")
        return;
    }
    if(newPassword.toString() !== confirmedPassword.toString()){
        alert("Passwords do not match.")
        return;
    }

    try{
        const response = await fetch('/api/auth/reset-forgotten-password', {
            method: 'POST',
            headers: {'Content-Type' : 'application/json'},
            body: JSON.stringify({email, otp, newPassword, confirmedPassword})
        });

        const data = await response.json();
        if (response.ok){
            alert(data.message);
            window.location.href = `/login`;
        }
        else{
            alert(data.message);
        }

    }catch (err){
        alert(err);
    }

}