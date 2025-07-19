import React from "react";
import Menu from "../components/Menu";

function ErreurPage() {
    return (
        <div>
            <Menu />
            <h1>Page d'erreur</h1>
            <p>Vous n'êtes pas autorisé à accéder à cette page ! Vous devez vous connecter à la page</p>
        </div>
    );
}

export default ErreurPage;