import http from 'k6/http';
import { sleep, check } from 'k6';

// Configuration du test
export let options = {
  // Scénario 1: Test de charge progressive
  stages: [
    { duration: '30s', target: 20 },  // Montée à 20 utilisateurs
    { duration: '1m', target: 50 },   // Montée à 50 utilisateurs
    { duration: '1m', target: 100 },  // Montée à 100 utilisateurs
    { duration: '30s', target: 0 },   // Descente à 0
  ],

  // Scénario 2: Test de seuil (décommenter pour activer)
  // thresholds: {
  //   http_req_duration: ['p(95)<500'], // 95% des requêtes < 500ms
  //   http_req_failed: ['rate<0.01'],   // Moins de 1% d'échecs
  // },
};

// Configuration des URLs des services
const services = {
  authentification: 'http://localhost:8087',
  candidate: 'http://localhost:8081',
  interview: 'http://localhost:8082',
  notification: 'http://localhost:8083',
  dashboard: 'http://localhost:8084',
  eureka: 'http://localhost:8762',
  gateway: 'http://localhost:8080',
};

export default function () {
  // Test 1: Health check de tous les services
  let healthChecks = [
    { url: `${services.authentification}/actuator/health`, name: 'Authentification' },
    { url: `${services.candidate}/actuator/health`, name: 'Candidate Service' },
    { url: `${services.interview}/actuator/health`, name: 'Interview Service' },
    { url: `${services.notification}/actuator/health`, name: 'Notification Service' },
    { url: `${services.dashboard}/actuator/health`, name: 'Dashboard Service' },
    { url: `${services.eureka}/actuator/health`, name: 'Eureka Server' },
    { url: `${services.gateway}/actuator/health`, name: 'API Gateway' },
  ];

  healthChecks.forEach(service => {
    let res = http.get(service.url);
    check(res, {
      [`${service.name} status was 200`]: (r) => r.status == 200,
    });
    sleep(0.5);
  });

  // Test 2: Login (Authentification)
  let loginPayload = JSON.stringify({
    email: 'test@example.com',
    password: 'password123'
  });

  let loginParams = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  let loginRes = http.post(
    `${services.authentification}/api/auth/login`,
    loginPayload,
    loginParams
  );

  check(loginRes, {
    'Login status was 200 or 401': (r) => r.status === 200 || r.status === 401,
  });

  // Test 3: Récupérer les candidats (si token disponible)
  let token = null;
  try {
    let tokenResponse = JSON.parse(loginRes.body);
    token = tokenResponse.token;
  } catch (e) {
    // Pas de token, continuer
  }

  if (token) {
    let authHeaders = {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    };

    let candidatesRes = http.get(
      `${services.candidate}/api/candidates`,
      authHeaders
    );

    check(candidatesRes, {
      'Get candidates status was 200': (r) => r.status === 200,
    });
  }

  // Pause entre les itérations
  sleep(1);
}

// Test spécifique pour un service
export function authentificationTest() {
  let res = http.get(`${services.authentification}/api/auth/health`);
  check(res, { 'Authentification is healthy': (r) => r.status === 200 });
}

// Test d'inscription
export function registerTest() {
  let randomId = Math.floor(Math.random() * 10000);
  let payload = JSON.stringify({
    email: `test${randomId}@example.com`,
    password: 'password123',
    nom: 'Test',
    prenom: 'User',
    role: 'CANDIDATE'
  });

  let params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  let res = http.post(
    `${services.authentification}/api/auth/register`,
    payload,
    params
  );

  check(res, {
    'Register status was 200': (r) => r.status === 200,
  });
}