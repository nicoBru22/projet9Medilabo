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
  const [errors, setErrors] = useState({});

  const handleSubmit = async (e) => {
    e.preventDefault();

    const newUser = { username, prenom, nom, password, role };
    const token = localStorage.getItem('jwtToken');
    
    try {
      const response = await fetch("http://localhost:8080/utilisateur/add", {
        method: "POST",
        headers: {         
                    'Authorization': `Bearer ${token}`,
                    "Content-Type": "application/json" },
        body: JSON.stringify(newUser),
        credentials: "include",
      });

      if (response.ok) {
        alert("Utilisateur ajouté avec succès !");
        navigate("/user/liste");
      } else {
          const errorData = await response.json();
          if (response.status === 400 && typeof errorData === "object") {
          setErrors(errorData);
        } else {
            alert("Erreur lors de l'ajout du patient.");
        }
      } 
    } 
    
    
    catch (error) {
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
              placeholder="Ex : nicolasb22"
              onChange={(e) => setUsername(e.target.value)}
            />
          </div>
          {errors.username && <div className="errorUser">{errors.username}</div>}

          <div className="elementForm">
            <label htmlFor="prenom" className="labelForm">Prénom :</label>
            <input
              type="text"
              id="prenom"
              name="prenom"
              value={prenom}
              onChange={(e) => setPrenom(e.target.value)}
              placeholder="Ex : Nicolas"
            />
          </div>
          {errors.prenom && <div className="errorUser">{errors.prenom}</div>}


          <div className="elementForm">
            <label htmlFor="nom" className="labelForm">Nom :</label>
            <input
              type="text"
              id="nom"
              name="nom"
              value={nom}
              onChange={(e) => setNom(e.target.value)}
              placeholder="Ex : BRUNET"
            />
          </div>
          {errors.nom && <div className="errorUser">{errors.nom}</div>}

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
          {errors.role && <div className="errorUser">{errors.role}</div>}

          <div className="elementForm">
            <label htmlFor="password" className="labelForm">Mot de passe :</label>
            <input
              type="password"
              id="password"
              name="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Ex : Password@123"
            />
          </div>
          {errors.password && <div className="errorUser">{errors.password}</div>}

          <button type="submit" className="buttonSubmitForm">
            Créer le compte utilisateur
          </button>
        </form>
      </div>
    </div>
  );
}

export default AddUserPage;
