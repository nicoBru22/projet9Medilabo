import React, { useState, useEffect } from 'react';

function AlertSante({ patientId }) {
  // 1. Déclarer un état pour stocker le résultat de l'API
  const [riskLevel, setRiskLevel] = useState('Chargement du risque...');
  const [error, setError] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  // 2. Utiliser useEffect pour appeler l'API quand le composant est monté ou que patientId change
  useEffect(() => {
    const token = localStorage.getItem('jwtToken');
    const fetchRiskLevel = async () => {
      try {

        const response = await fetch(`http://localhost:8080/alertes/detecte?patientId=${patientId}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      credentials: "include",
      });

        if (!response.ok) {
          const errorData = await response.text();
          throw new Error(`Erreur HTTP: ${response.status} - ${errorData}`);
        }

        const data = await response.text(); // Votre API renvoie une chaîne simple, donc use .text()
        setRiskLevel(data); // Mettre à jour l'état avec la réponse
      } catch (err) {
        console.error("Erreur lors de la récupération du niveau de risque:", err);
        setError(err.message); // Stocker le message d'erreur
        setRiskLevel('Erreur lors du chargement'); // Afficher un message d'erreur à l'utilisateur
      } finally {
        setIsLoading(false); // Indiquer que le chargement est terminé
      }
    };

    if (patientId) { // S'assurer qu'un patientId est disponible avant l'appel
      fetchRiskLevel();
    }
  }, [patientId]); // Le tableau de dépendances : re-déclenche l'effet si patientId change

  // 3. Rendu du composant
  if (isLoading) {
    return (
      <div style={{ padding: '20px', border: '1px solid #ddd', borderRadius: '5px', backgroundColor: '#f9f9f9' }}>
        <p>Calcul du risque pour le patient {patientId}...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div style={{ padding: '20px', border: '1px solid #f00', borderRadius: '5px', backgroundColor: '#ffe6e6', color: '#f00' }}>
        <p>Une erreur est survenue pour le patient {patientId} : <strong>{error}</strong></p>
        <p>Veuillez réessayer plus tard ou contacter l'administrateur.</p>
      </div>
    );
  }

  // Si tout s'est bien passé, afficher le niveau de risque
  let displayStyle = {};
  let message = `Le niveau de risque du patient ${patientId} est : `;

  // Personnalisation de l'affichage en fonction du niveau de risque
  switch (riskLevel) {
    case 'Borderline':
      displayStyle = { color: '#ff8c00', fontWeight: 'bold' }; // Orange
      message += "Attention, il est en zone limite.";
      break;
    case 'In Danger':
      displayStyle = { color: '#dc3545', fontWeight: 'bold' }; // Rouge
      message += "URGENT : Le patient est en danger !";
      break;
    case 'Early Onset':
      displayStyle = { color: '#8b0000', fontWeight: 'bold' }; // Rouge foncé
      message += "TRÈS URGENT : Début précoce de pathologie détecté !";
      break;
    case 'none':
      displayStyle = { color: '#28a745' }; // Vert
      message += "Aucune alerte détectée, tout va bien.";
      break;
    case 'Patient Not Found':
      displayStyle = { color: '#6c757d' }; // Gris
      message = `Patient ${patientId} non trouvé dans le système.`;
      break;
    case 'Error retrieving transmissions':
    case 'Error retrieving patient data':
    case 'Error retrieving patient age':
      displayStyle = { color: '#dc3545', fontStyle: 'italic' }; // Rouge
      message = `Erreur système lors de la récupération des données pour le patient ${patientId}.`;
      break;
    default:
      displayStyle = { color: '#6c757d' }; // Gris par défaut
      break;
  }

  return (
    <div style={{ padding: '20px', border: '1px solid #ddd', borderRadius: '5px', backgroundColor: '#f9f9f9', marginBottom: '15px' }}>
      <p>{message} <span style={displayStyle}>{riskLevel}</span></p>
    </div>
  );
}

export default AlertSante;