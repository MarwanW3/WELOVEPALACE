document.addEventListener("DOMContentLoaded", () => {

  // -------- REGISTER --------
  const registerForm = document.getElementById("registerForm");
  if (registerForm) {
    registerForm.addEventListener("submit", async (e) => {
      e.preventDefault();

      const username = document.getElementById("reg-username").value;
      const password = document.getElementById("reg-password").value;

      const res = await fetch("/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password })
      });

      alert(res.status === 201 ? "Registrerad!" : "Registrering misslyckades: " + res.status);
    });
  }

  // -------- LOGIN --------
  const loginForm = document.getElementById("loginForm");
  if (loginForm) {
    loginForm.addEventListener("submit", async (e) => {
      e.preventDefault();

      const username = document.getElementById("username").value;
      const password = document.getElementById("password").value;

      const res = await fetch("/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password })
      });

      if (res.ok) {
        window.location.href = "/";
      } else {
        alert("Login misslyckades");
      }

    });
  }

});
