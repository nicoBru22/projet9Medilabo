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
        </div>
    )
}

export default HomePage;