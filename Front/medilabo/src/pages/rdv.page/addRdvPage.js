import React, { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import Menu from "../../components/Menu";

function AddRdvPage() {
  const { id } = useParams();
  const token = localStorage.getItem("jwtToken");
  const navigate = useNavigate();

  const [nomMedecin, setNomMedecin] = useState("");
  const [jourRdv, setJourRdv] = useState("");
  const [heureRdv, setHeureRdv] = useState("");
  const [errors, setErrors] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    console.log("handleSubmit envoyé");

    const rdv = {
      id,
      nomMedecin,
      jourRdv,
      heureRdv,
    };

    try {
      if (!token) {
        navigate("/connexion");
        return;
      }

      const response = await fetch("http://localhost:8080/patient/addRdv", {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify(rdv),
      });

      if (response.ok) {
        alert("Rendez-vous ajouté avec succès !");
        navigate("/patient/Liste");
      } else {
        const errorData = await response.json();
        if (response.status === 400 && typeof errorData === "object") {
          setErrors(errorData);
        } else {
          alert("Erreur lors de l'ajout du rendez-vous.");
        }
      }
    } catch (error) {
      console.error("Erreur fetch :", error);
      alert("Erreur réseau ou serveur.");
    }
  };

  return (
    <div>
        <Menu />   
      <h1>Ajouter un rendez-vous</h1>
      <form onSubmit={handleSubmit}>
        <label>Nom du médecin :</label>
        <input
          type="text"
          value={nomMedecin}
          onChange={(e) => setNomMedecin(e.target.value)}
          required
        />

        <label>Jour du rendez-vous :</label>
        <input
          type="date"
          value={jourRdv}
          onChange={(e) => setJourRdv(e.target.value)}
          required
        />

        <label>Heure du rendez-vous :</label>
        <input
          type="time"
          value={heureRdv}
          onChange={(e) => setHeureRdv(e.target.value)}
          required
        />

        <button type="submit">Ajouter le rendez-vous</button>
      </form>
    </div>
  );
}

export default AddRdvPage;
