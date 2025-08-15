import React from "react";
import Menu from "../components/Menu";
import { Link } from "react-router-dom";
import "../styles/accueilPage.css";

function HomePage() {
    return (
        <div className="containerPageAccueil">
            <Menu />
            <div className="containerAccueil">
                <h1 className="titrePageAccueil">Bienvenue sur le site MediLabo</h1>
                <div className="containerElementAccueil">
                    <div className="navigationTitre">Navigation Patient :</div>
                    <Link className="linkAccueil" to="/patient/liste">- Afficher la liste des patients</Link>
                    <Link className="linkAccueil" to="/patient/add">- Ajouter un nouveau patient</Link>
                </div>
                <div className="containerElementAccueil">
                    <div className="navigationTitre">Navigation Utilisateur :</div>
                    <Link className="linkAccueil" to="/user/liste">- Afficher la liste des utilisateurs</Link>
                    <Link className="linkAccueil" to="/user/add">- Ajouter un nouvel utilisateur</Link>
                </div>
                <div className="containerElementAccueil">
                    <div className="navigationTitre">Documentation API :</div>
                    <Link className="linkAccueil" to="http://localhost:8081/swagger-ui/index.html" target="_blank" rel="noopener noreferrer">- Documentation Swagger UI de l'API Patient</Link>
                    <Link className="linkAccueil" to="http://localhost:8082/swagger-ui/index.html" target="_blank" rel="noopener noreferrer">- Documentation Swagger UI de l'API Note</Link>
                    <Link className="linkAccueil" to="http://localhost:8083/swagger-ui/index.html" target="_blank" rel="noopener noreferrer">- Documentation Swagger UI de l'API-Utilisateur</Link>
                    <Link className="linkAccueil" to="http://localhost:8084/swagger-ui/index.html" target="_blank" rel="noopener noreferrer">- Documentation Swagger UI de l'API Alerte</Link>
                </div>
            </div>
        </div>
    )
}

export default HomePage;