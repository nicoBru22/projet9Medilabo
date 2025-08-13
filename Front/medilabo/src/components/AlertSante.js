import React, { useState, useEffect } from 'react';

function AlertSante({ patientId }) {
  const [riskLevel, setRiskLevel] = useState('Chargement du risque...');
  const [error, setError] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('jwtToken');
    const fetchRiskLevel = async () => {
      try {

        const response = await fetch(`http://localhost:8080/alerte/detecte?patientId=${patientId}`, {
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

        const data = await response.text();
        setRiskLevel(data);
      } catch (err) {
        console.error("Erreur lors de la récupération du niveau de risque:", err);
        setError(err.message); // Stocker le message d'erreur
        setRiskLevel('Erreur lors du chargement');
      } finally {
        setIsLoading(false);
      }
    };

    if (patientId) {
      fetchRiskLevel();
    }
  }, [patientId]);

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

  let displayStyle = {};
  let message = `Risque de diabète : `;

  switch (riskLevel) {
    case 'Borderline':
      displayStyle = { color: '#ff8c00', fontWeight: 'bold' };
      message += "Attention, il est en zone limite.";
      break;
    case 'In Danger':
      displayStyle = { color: '#dc3545', fontWeight: 'bold' };
      message += "URGENT : Le patient est en danger !";
      break;
    case 'Early Onset':
      displayStyle = { color: '#8b0000', fontWeight: 'bold' };
      message += "TRÈS URGENT : Début précoce de pathologie détectée !";
      break;
    case 'none':
      displayStyle = { color: '#28a745' };
      message += "Aucune alerte détectée, tout va bien.";
      break;
    case 'Patient Not Found':
      displayStyle = { color: '#6c757d' };
      message = `Patient ${patientId} non trouvé dans le système.`;
      break;
    case 'Error retrieving transmissions':
    case 'Error retrieving patient data':
    case 'Error retrieving patient age':
      displayStyle = { color: '#dc3545', fontStyle: 'italic' };
      message = `Erreur système lors de la récupération des données pour le patient ${patientId}.`;
      break;
    default:
      displayStyle = { color: '#6c757d' };
      break;
  }

  return (
    <div style={{ padding: '20px', border: '1px solid #ddd', borderRadius: '5px', backgroundColor: '#f9f9f9', marginBottom: '15px' }}>
      <p>{message} <span style={displayStyle}>{riskLevel}</span></p>
    </div>
  );
}

export default AlertSante;