import React from "react";
import { Link } from "react-router-dom";
import { useNavigate } from 'react-router-dom';
import '../styles/menu.css';

function Menu() {
    const navigate = useNavigate();
    const token = localStorage.getItem('jwtToken');

const handleLogout = async () => {
  try {
    await fetch('/logout', {
      method: 'POST',
      credentials: 'include',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    });
  } catch (error) {
    console.error('Erreur lors du logout', error);
  } finally {
    localStorage.removeItem('jwtToken');
    alert("Vous vous êtes déconnecté. Vous allez être redirigé vers la page de connexion")
    navigate('/connexion');
  }
};



    return (
        <div className="containerNavigation">
            <nav className="menu">
                <Link to="/" className="linkNav">Accueil</Link>
                <Link to="/patient/liste" className="linkNav">Liste des patients</Link>
                <Link to="/patient/add" className="linkNav">Ajouter un patient</Link>
                <Link to="/user/liste" className="linkNav">Liste des utilisateurs</Link>
                <Link to="/user/add" className="linkNav">Ajouter un utilisateur</Link>
                {!token ? (
                    <Link to="/connexion" className="linkNav">Se connecter</Link>
                    ) : (
                    <button onClick={handleLogout} className="linkNav" style={{ background: 'none', border: 'none', cursor: 'pointer', padding: 0, font: 'inherit', color: 'inherit' }}>
                        Se déconnecter
                    </button>
                    )}
            </nav>
        </div>
    );

}

export default Menu;