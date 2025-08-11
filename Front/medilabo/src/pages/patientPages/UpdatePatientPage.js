import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import Menu from "../../components/Menu";

function UpdatePatientPage() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [prenom, setPrenom] = useState("");
  const [nom, setNom] = useState("");
  const [dateNaissance, setDateNaissance] = useState("");
  const [genre, setGenre] = useState("");
  const [adresse, setAdresse] = useState("");
  const [telephone, setTelephone] = useState("");
    const [error, setError] = useState(null);

  const token = localStorage.getItem('jwtToken');


  useEffect(() => {
    if (!token) {
      navigate("/connexion");
      return;
    }
    const fetchPatient = async () => {
      try {
        
        const response = await fetch(`http://localhost:8080/patient/infos/${id}`, {
          headers: {
            "Authorization": `Bearer ${token}`,
          },
          credentials: "include",
        });
        if (!response.ok) throw new Error("Erreur lors du chargement du patient");
        const data = await response.json();

        setPrenom(data.prenom);
        setNom(data.nom);
        setDateNaissance(data.dateNaissance);
        setGenre(data.genre);
        setAdresse(data.adresse);
        setTelephone(data.telephone);
      } catch (error) {
        console.error(error);
        alert("Impossible de charger les données du patient");
      }
    };

    fetchPatient();
  }, [id]);

  const handleSubmit = async (e) => {
    e.preventDefault();

    const patient = {
      id,
      prenom,
      nom,
      dateNaissance,
      genre,
      adresse,
      telephone,
    };

    try {
      const response = await fetch(`http://localhost:8080/patient/update/${id}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(patient),
      });

      if (response.ok) {
        alert("Patient modifié avec succès !");
        navigate("/patient/Liste");
      } else {
        alert("Erreur lors de la mise à jour du patient.");
      }
    } catch (error) {
      console.error("Erreur fetch :", error);
      alert("Erreur réseau ou serveur.");
    }
  };

  return (
    <div>
      <Menu />
      <h1>Modifier un patient</h1>

      <form onSubmit={handleSubmit}>
        <label htmlFor="prenom">Prénom</label>
        <input
          type="text"
          id="prenom"
          name="prenom"
          value={prenom}
          onChange={(e) => setPrenom(e.target.value)}
          required
        />

        <label htmlFor="nom">Nom</label>
        <input
          type="text"
          id="nom"
          name="nom"
          value={nom}
          onChange={(e) => setNom(e.target.value)}
          required
        />

        <label htmlFor="dateNaissance">Date de naissance</label>
        <input
          type="date"
          id="dateNaissance"
          name="dateNaissance"
          value={dateNaissance}
          onChange={(e) => setDateNaissance(e.target.value)}
          required
        />

        <label htmlFor="genre">Genre</label>
        <select
          id="genre"
          name="genre"
          value={genre}
          onChange={(e) => setGenre(e.target.value)}
          required
        >
          <option value="">-- Sélectionner --</option>
          <option value="masculin">Masculin</option>
          <option value="feminin">Féminin</option>
        </select>

        <label htmlFor="adresse">Adresse</label>
        <input
          type="text"
          id="adresse"
          name="adresse"
          value={adresse}
          onChange={(e) => setAdresse(e.target.value)}
          required
        />

        <label htmlFor="telephone">Téléphone</label>
        <input
          type="text"
          id="telephone"
          name="telephone"
          value={telephone}
          onChange={(e) => setTelephone(e.target.value)}
          required
        />

        <button type="submit">Modifier le patient</button>
      </form>
    </div>
  );
}

export default UpdatePatientPage;
