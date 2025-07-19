import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Menu from "../../components/Menu";

function AddTransmissionPage() {
  const [nomMedecin, setNomMedecin] = useState("");
  const [prenomMedecin, setPrenomMedecin] = useState("");
  const [transmission, setTransmission] = useState("");

  const [patient, setPatient] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

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

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log({ nomMedecin, prenomMedecin, transmission });

    const transmissionData = {
        patientId: id,
        nomMedecin: nomMedecin,
        prenomMedecin: prenomMedecin,
        transmission: transmission,
    };

  fetch("http://localhost:8080/transmission/add", {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      credentials: "include",
    body: JSON.stringify(transmissionData),
  })
  .then((response) => {
    if (!response.ok) {
      throw new Error("Erreur lors de l'envoi de la transmission");
    }
    return response.json();
  })
  .then((data) => {
    console.log("Transmission ajoutée :", data);
    alert("Transmission ajoutée avec succès !");
    navigate(`/patient/infos/${id}`);
  })
  .catch((error) => {
    console.error("Erreur:", error);
  });

  };

  if (isLoading) return <p>Chargement du patient...</p>;
  if (error) return <p>Erreur : {error}</p>;

  return (
    <div>
        <Menu />
      <h1>Page pour ajouter une transmission au patient : {patient.nom} {patient.prenom}</h1>

      <div className="containerForm">
        <form onSubmit={handleSubmit}>
          <div>
            <label className="labelFormTransmission" htmlFor="nomMedecin">
              Nom du Médecin :
            </label>
            <input
              id="nomMedecin"
              type="text"
              value={nomMedecin}
              onChange={(e) => setNomMedecin(e.target.value)}
            />

            <label className="labelFormTransmission" htmlFor="prenomMedecin">
              Prénom du Médecin :
            </label>
            <input
              id="prenomMedecin"
              type="text"
              value={prenomMedecin}
              onChange={(e) => setPrenomMedecin(e.target.value)}
            />
          </div>

          <label className="labelFormTransmission" htmlFor="transmission">
            Transmission :
          </label>
          <textarea
            id="transmission"
            value={transmission}
            onChange={(e) => setTransmission(e.target.value)}
          />

          <button type="submit">Ajouter la transmission</button>
        </form>
      </div>
    </div>
  );
}

export default AddTransmissionPage;
