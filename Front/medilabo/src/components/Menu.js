import React from "react";
import { Link } from "react-router-dom";
import '../styles/menu.css';

function Menu() {

    return (
        <div className="containerNavigation">
            <nav className="menu">
                <Link to="/" className="linkNav">Accueil</Link>
                <Link to="/patient/liste" className="linkNav">Liste des patients</Link>
                <Link to="/patient/add" className="linkNav">Ajouter un patient</Link>
                <Link to="/user/liste" className="linkNav">Liste des utilisateurs</Link>
                <Link to="/user/add" className="linkNav">Ajouter un utilisateur</Link>
                <Link to="/connexion" className="linkNav">Se connecter</Link>
            </nav>
        </div>
    );

}

export default Menu;