import React, { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import AlertSante from '../../components/AlertSante';
import Menu from "../../components/Menu";
import "../../styles/infosPatientPage.css"

function InfosPatientPage() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [patient, setPatient] = useState(null);
const [medecinsMap, setMedecinsMap] = useState({});
  const [medecinIds, setMedecin] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [notes, setNotes] = useState([]);

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
  const token = localStorage.getItem('jwtToken');
  if (!token) {
    navigate("/connexion");
    return;
  }

  fetch(`http://localhost:8080/note/getNotesPatient?patientId=${id}`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
    credentials: "include",
  })
    .then(async (response) => {
      if (!response.ok) throw new Error("Erreur lors du chargement des notes");
      const notesData = await response.json();
      setNotes(notesData);

      const medecinIds = [...new Set(notesData.map(note => note.medecinId))];

      if (medecinIds.length > 0) {
        return fetch(`http://localhost:8080/utilisateur/getUsersByIds?ids=${medecinIds.join(",")}`, {
          method: "GET",
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
          credentials: "include",
        });
      }
      return null;
    })
    .then(async (res) => {
      if (res && !res.ok) throw new Error("Erreur lors du chargement des médecins");
      if (res) {
        const medecinsData = await res.json();
        setMedecinsMap(medecinsData);
      }
    })
    .catch((err) => {
      console.error("Erreur lors du chargement des notes ou médecins:", err);
    });
}, [id, navigate]);

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
                    ?.slice()
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
          </div>

          <div className="containerNote">
            <p className="note"><strong>Notes :</strong></p> 
            <ul>
              {notes.length > 0 ? (
                notes.map((note) => {
                  const medecin = medecinsMap[note.medecinId];
                  return (
                    <li className="noteList" key={note.id}>
                      <div className="infosNote">
                        <div className="dateNote">Date : {note.dateNote}</div>
                        <div className="nomPrenomMedecin">
                          Dr {medecin ? `${medecin.prenom} ${medecin.nom}` : "Chargement..."}
                        </div>
                      </div>
                      <div className="notenEcrite">{note.note}</div>
                    </li>
                  )
                })
              ) : (
                <li className="aucuneNote">Aucune note</li>
              )}
            </ul>
            <button className="btnAjouterNote" onClick={() => navigate(`/patient/infos/${patient.id}/note`)}>Ajouter une note</button>
          </div>

        </div>
      </div>
    </div>
  );
}

export default InfosPatientPage;
