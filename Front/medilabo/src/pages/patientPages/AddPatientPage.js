import React, { useState } from "react";
import Menu from "../../components/Menu";
import { useNavigate } from "react-router-dom";
import '../../styles/addPatientPage.css'

function AddPatientPage() {
  // États pour chaque champ du formulaire
  const [prenom, setPrenom] = useState("");
  const [nom, setNom] = useState("");
  const [dateNaissance, setDateNaissance] = useState("");
  const [genre, setGenre] = useState("");
  const [adresse, setAdresse] = useState("");
  const [telephone, setTelephone] = useState("");

  const navigate = useNavigate(); 
  const token = localStorage.getItem('jwtToken');

  const handleSubmit = async (e) => {
    e.preventDefault(); // Empêche le rechargement de la page
    console.log("handleSubmit envoyé")

    const patient = {
      prenom,
      nom,
      dateNaissance,
      genre,
      adresse,
      telephone,
    };

    try {
      if (!token) {
        navigate("/connexion");
        return;
      }
      const response = await fetch("http://localhost:8080/patient/add", {
        method: "POST",
        headers: {
        'Authorization': `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify(patient),
      });

      if (response.ok) {
        alert("Patient ajouté avec succès !");
        navigate("/patient/Liste")
      } else {
        alert("Erreur lors de l'ajout du patient.");
      }
    } catch (error) {
      console.error("Erreur fetch :", error);
      alert("Erreur réseau ou serveur.");
    }
  };

  return (
    <div>
      <Menu />
      <h1 className="titrePage">Page pour ajouter un nouveau patient</h1>
      <div className="containerForm">
        <form onSubmit={handleSubmit} className="formPatient">

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
            <label htmlFor="dateNaissance" className="labelForm">Date de naissance :</label>
            <input
              type="date"
              id="dateNaissance"
              name="dateNaissance"
              value={dateNaissance}
              onChange={(e) => setDateNaissance(e.target.value)}
            />
          </div>

          <div className="elementForm">
          <label htmlFor="genre" className="labelForm">Genre :</label>
          <select
            id="genre"
            name="genre"
            value={genre}
            onChange={(e) => setGenre(e.target.value)}
          >
            <option value="">-- Sélectionner --</option>
            <option value="masculin">Masculin</option>
            <option value="feminin">Féminin</option>
          </select>
          </div>

          <div className="elementForm">
          <label htmlFor="adresse" className="labelForm">Adresse :</label>
          <input
            type="text"
            id="adresse"
            name="adresse"
            value={adresse}
            onChange={(e) => setAdresse(e.target.value)}
          />
          </div>

          <div className="elementForm">
          <label htmlFor="telephone" className="labelForm">Téléphone :</label>
          <input
            type="text"
            id="telephone"
            name="telephone"
            value={telephone}
            onChange={(e) => setTelephone(e.target.value)}
          />
          </div>

          <button type="submit" className="buttonSubmitForm">Ajouter le patient</button>
        </form>
      </div>
    </div>
  );
}

export default AddPatientPage;
