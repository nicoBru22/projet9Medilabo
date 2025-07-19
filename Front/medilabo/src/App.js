import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

import AccueilPage from './pages/Accueil.js';
import ListPatientPage from './pages/patientPages/ListPatientPage.js';
import AddPatientPage from './pages/patientPages/AddPatientPage.js';
import UpdatePatientPage from './pages/patientPages/UpdatePatientPage.js';
import ListUserPage from './pages/userPages/ListUserPage.js';
import AddUserPage from './pages/userPages/AddUserPage.js';
import InfosPatientPage from './pages/patientPages/InfosPatientPage.js';
import AddTransmissionPage from './pages/transmissionPages/AddTransmissionPage.js';
import LoginPage from './pages/LoginPage.js';
import ErreurPage from './pages/Erreur.js';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<AccueilPage />} />
        <Route path="/patient/liste" element={<ListPatientPage />} />
        <Route path="/patient/add" element={<AddPatientPage />} />
        <Route path="/patient/update/:id" element={<UpdatePatientPage />} />
        <Route path="/user/liste" element={<ListUserPage />} />
        <Route path="/user/add" element={<AddUserPage />} />
        <Route path="/patient/infos/:id" element={<InfosPatientPage />} />
        <Route path="/patient/infos/:id/transmission" element={<AddTransmissionPage />} />
        <Route path="/connexion" element={<LoginPage />} />
        <Route path="/erreur" element={<ErreurPage />} />
      </Routes>
    </Router>
  );
}

export default App;
