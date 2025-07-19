import React, { useState } from "react";
import Menu from "../../components/Menu";
import { useNavigate } from "react-router-dom";

function AddUserPage() {
  const [username, setUsername] = useState("");
  const [prenom, setPrenom] = useState("");
  const [nom, setNom] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!prenom || !nom || !password) {
      alert("Tous les champs sont obligatoires");
      return;
    }

    const newUser = { username, prenom, nom, password, role };
    const token = localStorage.getItem('jwtToken');
    
    try {
      const response = await fetch("http://localhost:8080/utilisateur/add", {
        method: "POST",
        headers: {         
                    'Authorization': `Bearer ${token}`,
                    "Content-Type": "application/json" },
        body: JSON.stringify(newUser),
      });

      if (response.ok) {
        alert("Utilisateur ajouté avec succès !");
        navigate("/user/liste");
      } else {
        alert("Erreur lors de l'ajout de l'utilisateur.");
      }
    } catch (error) {
      console.error("Erreur fetch :", error);
      alert("Erreur réseau ou serveur.");
    }
  };

  return (
    <div>
      <Menu />
      <h1 className="titrePage">Page pour ajouter un nouvel utilisateur</h1>
      <div className="containerForm">
        <form onSubmit={handleSubmit} className="formPatient">
          <div className="elementForm">
            <label htmlFor="username" className="labelForm">Username :</label>
            <input
              type="text"
              id="username"
              name="username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
            />
          </div>
          <div className="elementForm">
            <label htmlFor="prenom" className="labelForm">Prénom :</label>
            <input
              type="text"
              id="prenom"
              name="prenom"
              value={prenom}
              onChange={(e) => setPrenom(e.target.value)}
            />
          </div>

          <div className="elementForm">
            <label htmlFor="nom" className="labelForm">Nom :</label>
            <input
              type="text"
              id="nom"
              name="nom"
              value={nom}
              onChange={(e) => setNom(e.target.value)}
            />
          </div>
          <div className="elementForm">
            <label htmlFor="role" className="labelForm">Nom :</label>
            <select
              type="select"
              id="role"
              name="role"
              value={role}
              onChange={(e) => setRole(e.target.value)}
            >
              <option value="">-- Sélectionner un rôle --</option>
              <option value="administrateur">Administrateur</option>
              <option value="secretaire">Secrétaire</option>
              <option value="medecin">Médecin</option>
          </select>
          </div>

          <div className="elementForm">
            <label htmlFor="password" className="labelForm">Mot de passe :</label>
            <input
              type="password"
              id="password"
              name="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>

          <button type="submit" className="buttonSubmitForm">
            Créer le compte utilisateur
          </button>
        </form>
      </div>
    </div>
  );
}

export default AddUserPage;
