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
import AddRdvPage from './pages/rdv.page/addRdvPage.js';
import LoginPage from './pages/LoginPage.js';
import ErreurPage from './pages/Erreur.js';
import PrivateRoute from './components/PrivateRoute.js';


function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<AccueilPage />} />
        <Route path="/patient/liste" element={<PrivateRoute><ListPatientPage /></PrivateRoute>} />
        <Route path="/patient/add" element={<PrivateRoute><AddPatientPage /></PrivateRoute>} />
        <Route path="/patient/update/:id" element={<PrivateRoute><UpdatePatientPage /></PrivateRoute>} />
        <Route path="/user/liste" element={<PrivateRoute><ListUserPage /></PrivateRoute>} />
        <Route path="/user/add" element={<PrivateRoute><AddUserPage /></PrivateRoute>} />
        <Route path="/patient/infos/:id" element={<PrivateRoute><InfosPatientPage /></PrivateRoute>} />
        <Route path="/patient/rdv/:id" element={<PrivateRoute><AddRdvPage /></PrivateRoute>} />
        <Route path="/patient/infos/:id/transmission" element={<PrivateRoute><AddTransmissionPage /></PrivateRoute>} />
        <Route path="/connexion" element={<LoginPage />} />
        <Route path="/erreur" element={<ErreurPage />} />
      </Routes>
    </Router>
  );
}

export default App;
