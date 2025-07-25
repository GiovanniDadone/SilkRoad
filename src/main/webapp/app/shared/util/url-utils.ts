export const getLoginUrl = () => {
  const port = location.port ? `:${location.port}` : '';
  return `//${location.hostname}${port}/login`;
};
export const REDIRECT_URL = 'redirectURL';
