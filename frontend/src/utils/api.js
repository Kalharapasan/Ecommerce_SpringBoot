import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8000'
});

export function extractErrorMessage(err) {
  if (err.response && err.response.data) {
    if (typeof err.response.data === 'string') {
      return err.response.data;
    }
    if (typeof err.response.data === 'object' && err.response.data.message) {
      return err.response.data.message;
    }
    return JSON.stringify(err.response.data);
  }
  return err.message || 'An error occurred';
}

export function formatPrice(value) {
  if (value === undefined || value === null) return 'LKR 0.00';
  const num = Number(value);
  return 'LKR ' + num.toLocaleString('en-US', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  });
}

export default api;
