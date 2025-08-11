import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../../styles/listPatientPage.css';

import Menu from '../../components/Menu';

function ListPatientPage() {
  const [patients, setPatients] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('jwtToken');

    fetch('http://localhost:8080/patient/list', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      credentials: "include",
      })
      .then((response) => {
        if (!response.ok) {
          throw new Error('Erreur lors du chargement des patients');
        }
        return response.json();
      })
      .then((data) => {
        setPatients(data);
        setIsLoading(false);
      })
      .catch((error) => {
        setError(error.message);
        setIsLoading(false);
      });
  }, []);

    const handleDelete = async (id) => {
    if (!window.confirm("Voulez-vous vraiment supprimer ce patient ?")) return;

    try {
      const token = localStorage.getItem('jwtToken');
      
      const response = await fetch(`http://localhost:8080/patient/delete/${id}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
      },
      credentials: "include",
      });

      if (response.ok) {
        setPatients(patients.filter((patient) => patient.id !== id));
      } else {
        alert("Erreur lors de la suppression.");
      }
    } catch (err) {
      console.error("Erreur lors de la suppression :", err);
      alert("Erreur réseau.");
    }
  };

  if (isLoading) return <p>Chargement en cours...</p>;
  if (error) return <p>Erreur : {error}</p>;

return (
<div className="patient-list-container" id="patientListSection">
  <Menu />
  <h2 className="section-title">Liste des patients</h2>
  <table className="tablePatient">
    <thead>
      <tr className="ligneTablePatient">
        <th className="enTeteTableauElement">Prénom</th>
        <th className="enTeteTableauElement">Nom</th>
        <th className="enTeteTableauElement">Date de naissance</th>
        <th className="enTeteTableauElement">Genre</th>
        <th className="enTeteTableauElement">Adresse</th>
        <th className="enTeteTableauElement">Téléphone</th>
        <th className="enTeteTableauElement">Rendez-vous</th>
        <th className="enTeteTableauElement actions-column">Action</th>        
      </tr>
    </thead>
    <tbody>
      {patients.map((patient) => (
        <tr key={patient.id} className="lignePatient">
          <td className="elementLignePatient">{patient.prenom}</td>
          <td className="elementLignePatient">{patient.nom}</td>
          <td className="elementLignePatient">{patient.dateNaissance}</td>
          <td className="elementLignePatient">{patient.genre}</td>
          <td className="elementLignePatient">{patient.adresse}</td>
          <td className="elementLignePatient">{patient.telephone}</td>
          <td className="elementLignePatient">
              <ul>
                {patient.rdvList?.map(rdv => (
                  <li key={rdv.id}>{rdv.jourRdv} - {rdv.heureRdv}</li>
                ))}
              </ul>
          </td>
           <td className="elementLignePatient elementAction">      
            <button
              className="btnTablePatient btnModify"
              onClick={() => navigate(`/patient/update/${patient.id}`)}>Modifier</button>
            <button
              className="btnTablePatient btnDelete"
              onClick={() => handleDelete(patient.id)}>Supprimer</button>
            <button 
              className='btnTablePatient-infos btnInfos' 
              onClick={() => navigate(`/patient/infos/${patient.id}`)}>Informations</button>
            <button 
              className='btnTablePatient-infos btnRdv' 
              onClick={() => navigate(`/patient/rdv/${patient.id}`)}>Redez-vous</button>
          </td>
        </tr>
      ))}
    </tbody>
  </table>
</div>

);
}

export default ListPatientPage;
