import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import Menu from "../components/Menu";

function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState(null);

  const navigate = useNavigate(); // hook de navigation

 const handleSubmit = async (e) => {
  e.preventDefault();
  setError(null);

  try {
    const response = await fetch("http://localhost:8080/utilisateur/login", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ username, password }),
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || "Nom d'utilisateur ou mot de passe incorrect");
    }

    const data = await response.json();

    if (data.status === "success" && data.token) {
      localStorage.setItem("jwtToken", data.token);
      navigate("/");
    } else {
      setError("Erreur inattendue lors de la connexion");
    }
  } catch (err) {
    setError(err.message);
  }
};


  return (
    <div>
      <Menu />
      <h1>Bienvenue sur la page de connexion de MediLabo</h1>
      <form onSubmit={handleSubmit}>
        <label>
          Nom d'utilisateur :{" "}
          <input
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
        </label>
        <br />
        <label>
          Mot de passe :{" "}
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </label>
        <br />
        <button type="submit">Se connecter</button>
      </form>
      {error && <p style={{ color: "red" }}>{error}</p>}
    </div>
  );
}

export default LoginPage;
