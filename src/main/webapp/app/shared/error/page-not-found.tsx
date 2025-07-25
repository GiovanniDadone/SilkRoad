import React from 'react';
import { Translate } from 'react-jhipster';
import { Alert } from 'reactstrap';

const PageNotFound = () => {
  return (
    <div>
      <Alert color="danger" timeout={300}>
        <Translate contentKey="error.http.404">The page does not exist.</Translate>
      </Alert>
    </div>
  );
};

export default PageNotFound;
