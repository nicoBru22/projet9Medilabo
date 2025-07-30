import React from "react";
import Menu from "../components/Menu";
import { Link } from "react-router-dom";

function HomePage() {
    return (
        <div>
            <Menu />
            <h1>Bienvenue sur le site MediLabo</h1>

            <Link to="/patient/Liste">afficher la liste des patients</Link>
            <Link to="/medecin/Liste">afficher la liste des médecins</Link>
            <Link to="/patient/add">Ajouter un nouveau patient</Link>
            <Link to="/medecin/add">Ajouter un nouveau médecin</Link>
            <Link to="http://localhost:8081/swagger-ui/index.html" target="_blank" rel="noopener noreferrer">Accéder à la documentation Swagger UI de l'API Patient</Link>
            <Link to="http://localhost:8082/swagger-ui/index.html" target="_blank" rel="noopener noreferrer">Accéder à la documentation Swagger UI de l'API transmission</Link>
            <Link to="http://localhost:8083/swagger-ui/index.html" target="_blank" rel="noopener noreferrer">Accéder à la documentation Swagger UI de l'API-Utilisateur</Link>
            <Link to="http://localhost:8084/swagger-ui/index.html" target="_blank" rel="noopener noreferrer">Accéder à la documentation Swagger UI de l'API Anticipation</Link>
          <Link to="http://localhost:8081/swagger-ui/index.html" target="_blank" rel="noopener noreferrer">Accéder à la documentation Swagger UI de l'API Patient</Link>

        </div>
    )
}

export default HomePage;