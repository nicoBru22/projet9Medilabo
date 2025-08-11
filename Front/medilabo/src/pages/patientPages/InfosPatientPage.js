import React, { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import AlertSante from '../../components/AlertSante';
import Menu from "../../components/Menu";
import "../../styles/infosPatientPage.css"

function InfosPatientPage() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [patient, setPatient] = useState(null);
  const [medecins, setMedecins] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const token = localStorage.getItem('jwtToken');
    if (!token) {
      navigate("/connexion");
      return;
    }

    fetch(`http://localhost:8080/patient/infos/${id}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      credentials: "include",
    })
    .then(async (response) => {
      if (!response.ok) throw new Error("Erreur lors du chargement du patient");
      const data = await response.json();
      setPatient(data);
      setIsLoading(false);
    })
    .catch((err) => {
      setError(err.message);
      setIsLoading(false);
    });
  }, [id, navigate]);

  useEffect(() => {
    if (patient) {
      fetch(`http://localhost:8080/patient/medecinByPatient?id=${patient.id}`)
        .then((response) => {
          if (!response.ok) throw new Error("Erreur lors du chargement des médecins");
          return response.json();
        })
        .then((data) => setMedecins(data))
        .catch((error) => console.error(error));
    }
  }, [patient]);

  if (isLoading) return <p>Chargement...</p>;
  if (error) return <p>Erreur : {error}</p>;

  return (
    <div className="containerInfosPatientPageEtMenu">
      <Menu />
      <div className="containerInfosPatientPage">
        <h1>Informations sur le patient {patient.prenom} {patient.nom}</h1>
        <div className="infoPatient-transmissions">
          <div className="infoPatient-medecinRef">
            <div className="infosPatient">
              <p className="idPatient"><strong>ID :</strong> {patient.id}</p>
              <p className="nomPatient"><strong>Nom :</strong> {patient.nom}</p>
              <p className="prenomPatient"><strong>Prénom :</strong> {patient.prenom}</p>
              <p className="genre"><strong>Genre :</strong> {patient.genre}</p>
              <p className="dateNaissance"><strong>Date de naissance :</strong> {patient.dateNaissance}</p>
              <p className="adresse"><strong>Adresse :</strong> {patient.adresse}</p>
              <p className="telephone"><strong>Téléphone :</strong> {patient.telephone}</p>
              <p className="dateCréation"><strong>Dossier créé le :</strong> {patient.dateCreation}</p>
              <p className="dateModification"><strong>Dossier modifié le :</strong> {patient.dateModification}</p>
              <p className="listeRdv"><strong>Liste des rendez-vous :</strong>
                <ul>
                  {patient.rdvList
                    ?.slice() // on copie pour éviter de modifier l’original
                    .sort((a, b) => {
                      const dateA = new Date(`${a.jourRdv}T${a.heureRdv}`);
                      const dateB = new Date(`${b.jourRdv}T${b.heureRdv}`);
                      return dateA - dateB;
                    })
                    .map(rdv => (
                      <li key={rdv.id}>{rdv.jourRdv} - {rdv.heureRdv}</li>
                    ))}
                </ul>
              </p>    
            </div>
            <div>
              <AlertSante patientId={id} />
            </div>

            <div className="containerMedecinRef">
              <p className="medecinRef"><strong>Médecins référents :</strong></p> 
              <ul>
                {medecins.length > 0 ? (
                  medecins.map((medecin) => (
                    <li className="medecinList" key={medecin.id}>{medecin.prenom} {medecin.nom}</li>
                  ))
                ) : (
                  <li className="aucunMedecin">Aucun médecin référent</li>
                )}
              </ul>
              <button className="btnAjouterMedecin">Ajouter un médecin référent</button>
            </div>
          </div>

          <div className="containerTransmission">
            <p className="transmission"><strong>Transmissions :</strong></p> 
            <ul>
              {Array.isArray(patient.transmissionsList) && patient.transmissionsList.length > 0 ? (
                patient.transmissionsList.map((transmission) => (
                  <li className="transmissionList" key={transmission.id}>
                    <div className="infosTransmission">
                      <div className="dateTransmission">Date : {transmission.dateTransmission}</div>
                      <div className="nomPrenomMedecin">Dr {transmission.nomMedecin} {transmission.prenomMedecin}</div>
                    </div>
                    <div className="transmissionEcrite">{transmission.transmission}</div>
                  </li>
                ))
              ) : (
                <li className="aucuneTransmission">Aucune transmission</li>
              )}
            </ul>

            <button className="btnAjouterTransmission" onClick={() => navigate(`/patient/infos/${patient.id}/transmission`)}>Ajouter une transmission</button>
          </div>

        </div>
      </div>
    </div>
  );
}

export default InfosPatientPage;
