import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Menu from "../../components/Menu";

function AddNotePage() {
  const [nomMedecin, setNomMedecin] = useState("");
  const [prenomMedecin, setPrenomMedecin] = useState("");
  const [note, setNote] = useState("");

  const [patient, setPatient] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [errors, setErrors] = useState(null);

  const { id } = useParams();
  const navigate = useNavigate();

  const token = localStorage.getItem('jwtToken');

  useEffect(() => {
    if (!token) {
      navigate("/connexion");
      return;
    }
    setIsLoading(true);
    fetch(`http://localhost:8080/patient/infos/${id}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      credentials: "include",
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Erreur lors du chargement du patient");
        }
        return response.json();
      })
      .then((data) => {
        setPatient(data);
        setIsLoading(false);
      })
      .catch((error) => {
        setError(error.message);
        setIsLoading(false);
      });
  }, [id]);

  const handleSubmit = async (e) => {
  e.preventDefault();
  setErrors(null);

  try {
    if (!token) {
      navigate("/connexion");
      return;
    }

    const transmissionData = {
      patientId: id,
      nomMedecin,
      prenomMedecin,
      note,
    };

    const response = await fetch(`http://localhost:8080/note/add`, {
      method: "POST",
      headers: {
        'Authorization': `Bearer ${token}`,
        "Content-Type": "application/json",
      },
      credentials: "include",
      body: JSON.stringify(transmissionData),
    });

    if (response.ok) {
      alert("Transmission ajoutée avec succès !");
      navigate(`/patient/infos/${id}`);
    } else {
      let errorData;
      try {
        errorData = await response.json();
      } catch {
        errorData = null;
      }

      if (response.status === 400 && typeof errorData === "object") {
        setErrors(errorData);
        console.log("Erreur backend reçue :", errorData);
      } else {
        alert("Erreur lors de l'envoi de la transmission.");
      }
    }
  } catch (error) {
    console.error("Erreur fetch :", error);
    alert("Erreur réseau ou serveur.");
  }
};

  if (isLoading) return <p>Chargement du patient...</p>;
  if (error) return <p>Erreur : {error}</p>;

  return (
    <div>
        <Menu />
      <h1>Page pour ajouter une note au patient : {patient.nom} {patient.prenom}</h1>

      <div className="containerForm">
        <form onSubmit={handleSubmit}>
          <div>
            <label className="labelFormNote" htmlFor="nomMedecin">
              Nom du Médecin :
            </label>
            <input
              id="nomMedecin"
              type="text"
              value={nomMedecin}
              onChange={(e) => setNomMedecin(e.target.value)}
            />
            {errors?.nomMedecin && <p style={{ color: "red" }}>{errors.nomMedecin}</p>}

            <label className="labelFormNote" htmlFor="prenomMedecin">
              Prénom du Médecin :
            </label>
            <input
              id="prenomMedecin"
              type="text"
              value={prenomMedecin}
              onChange={(e) => setPrenomMedecin(e.target.value)}
            />
          </div>
          {errors?.prenomMedecin && <p style={{ color: "red" }}>{errors.prenomMedecin}</p>}

          <label className="labelFormNote" htmlFor="note">
            Note :
          </label>
          <textarea
            id="note"
            value={note}
            onChange={(e) => setNote(e.target.value)}
          />
          {errors?.note && <p style={{ color: "red" }}>{errors.note}</p>}

          <button type="submit">Ajouter la note</button>
        </form>
      </div>
    </div>
  );
}

export default AddNotePage;