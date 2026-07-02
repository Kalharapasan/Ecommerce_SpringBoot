export function decodeToken(token) {
  if (!token) return null;
  try {
    const payload = token.split('.')[1];
    const decoded = JSON.parse(atob(payload.replace(/-/g, '+').replace(/_/g, '/')));
    return decoded; // { username, userId, iss, iat }
  } catch (e) {
    return null;
  }
}
