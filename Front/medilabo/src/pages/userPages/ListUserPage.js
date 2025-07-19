import React from "react";
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";


import Menu from "../../components/Menu";


function ListUserPage() {
  const [user, setUser] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  const navigate = useNavigate();

  const token = localStorage.getItem('jwtToken');

  useEffect(() => {
    if (!token) {
      navigate("/connexion");
      return;
    }
    
    fetch("http://localhost:8080/utilisateur/list", {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      credentials: "include",
      })
      .then((response) => {
        if (!response.ok) {
          if (response.status === 401 || response.status === 403) {
            navigate("/erreur");
          } else {
            throw new Error("Erreur lors du chargement des utilisateurs");
          }
        }
        return response.json();
      })
      .then((data) => {
        setUser(data);
        setIsLoading(false);
      })
      .catch((error) => {
        setError(error.message);
        setIsLoading(false);
      });
  }, [navigate]);


    const handleDelete = async (id) => {
    if (!window.confirm("Voulez-vous vraiment supprimer cet utilisateur ?")) return;

    try {
      const response = await fetch(`http://localhost:8080/utilisateur/delete/${id}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
      },
      credentials: "include",
      });

      if (response.ok) {
        setUser(user.filter((user) => user.id !== id));
      } else {
        alert("Erreur lors de la suppression de l'utilisateur.");
      }
    } catch (err) {
      console.error("Erreur lors de la suppression :", err);
      alert("Erreur réseau.");
    }
  };

  if (isLoading) return <p>Chargement en cours...</p>;
  if (error) return <p>Erreur : {error}</p>;   




    return (
        <div className="containerListMedecinPage">
            <Menu />
              <h2 className="section-title">Liste des médecins</h2>
  <table className="tablePatient">
    <thead>
      <tr className="ligneTableUtilisateur">
        <th className="enTeteTableauElement">Username</th>
        <th className="enTeteTableauElement">Prénom</th>
        <th className="enTeteTableauElement">Nom</th>
        <th className="enTeteTableauElement">Password</th>
        <th className="enTeteTableauElement">Role</th>
        <th className="enTeteTableauElement actions-column">Action</th>        
      </tr>
    </thead>
    <tbody>
      {user.map((user) => (
        <tr key={user.id} className="lignePatient">
          <td className="elementLigneUser">{user.username}</td>
          <td className="elementLigneUser">{user.prenom}</td>
          <td className="elementLigneUser">{user.nom}</td>
          <td className="elementLigneUser">{user.password}</td>
          <td className="elementLigneUser">{user.role}</td>
           <td className="elementLigneUser elementAction">      
            <button
              className="btn-modify"
              onClick={() => navigate(`/user/update/${user.id}`)}
            >
              Modifier
            </button>
            <button
              className="btn-delete"
              onClick={() => handleDelete(user.id)}
            >
              Supprimer
            </button>
          </td>
        </tr>
      ))}
    </tbody>
  </table>
        </div>
    );
}

export default ListUserPage;