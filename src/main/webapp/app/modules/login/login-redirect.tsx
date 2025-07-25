import { useEffect } from 'react';
import { REDIRECT_URL } from 'app/shared/util/url-utils';
import { useLocation } from 'react-router';

export const LoginRedirect = () => {
  const pageLocation = useLocation();
  const from = pageLocation.state?.from || '/';

  useEffect(() => {
    localStorage.setItem(REDIRECT_URL, from);
    window.location.href = '/oauth2/authorization/oidc';
  });

  return null;
};

export default LoginRedirect;
