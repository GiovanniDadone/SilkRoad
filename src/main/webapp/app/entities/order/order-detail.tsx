import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './order.reducer';

export const OrderDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const orderEntity = useAppSelector(state => state.order.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="orderDetailsHeading">
          <Translate contentKey="silkRoadApp.order.detail.title">Order</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{orderEntity.id}</dd>
          <dt>
            <span id="orderDate">
              <Translate contentKey="silkRoadApp.order.orderDate">Order Date</Translate>
            </span>
          </dt>
          <dd>{orderEntity.orderDate ? <TextFormat value={orderEntity.orderDate} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="totalAmount">
              <Translate contentKey="silkRoadApp.order.totalAmount">Total Amount</Translate>
            </span>
          </dt>
          <dd>{orderEntity.totalAmount}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="silkRoadApp.order.status">Status</Translate>
            </span>
          </dt>
          <dd>{orderEntity.status}</dd>
          <dt>
            <Translate contentKey="silkRoadApp.order.customer">Customer</Translate>
          </dt>
          <dd>{orderEntity.customer ? orderEntity.customer.email : ''}</dd>
        </dl>
        <Button tag={Link} to="/order" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/order/${orderEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default OrderDetail;
