import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getCustomers } from 'app/entities/customer/customer.reducer';
import { OrderStatus } from 'app/shared/model/enumerations/order-status.model';
import { createEntity, getEntity, reset, updateEntity } from './order.reducer';

export const OrderUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const customers = useAppSelector(state => state.customer.entities);
  const orderEntity = useAppSelector(state => state.order.entity);
  const loading = useAppSelector(state => state.order.loading);
  const updating = useAppSelector(state => state.order.updating);
  const updateSuccess = useAppSelector(state => state.order.updateSuccess);
  const orderStatusValues = Object.keys(OrderStatus);

  const handleClose = () => {
    navigate(`/order${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getCustomers({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    values.orderDate = convertDateTimeToServer(values.orderDate);
    if (values.totalAmount !== undefined && typeof values.totalAmount !== 'number') {
      values.totalAmount = Number(values.totalAmount);
    }

    const entity = {
      ...orderEntity,
      ...values,
      customer: customers.find(it => it.id.toString() === values.customer?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          orderDate: displayDefaultDateTime(),
        }
      : {
          status: 'PENDING',
          ...orderEntity,
          orderDate: convertDateTimeFromServer(orderEntity.orderDate),
          customer: orderEntity?.customer?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="silkRoadApp.order.home.createOrEditLabel" data-cy="OrderCreateUpdateHeading">
            <Translate contentKey="silkRoadApp.order.home.createOrEditLabel">Create or edit a Order</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="order-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('silkRoadApp.order.orderDate')}
                id="order-orderDate"
                name="orderDate"
                data-cy="orderDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('silkRoadApp.order.totalAmount')}
                id="order-totalAmount"
                name="totalAmount"
                data-cy="totalAmount"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField label={translate('silkRoadApp.order.status')} id="order-status" name="status" data-cy="status" type="select">
                {orderStatusValues.map(orderStatus => (
                  <option value={orderStatus} key={orderStatus}>
                    {translate(`silkRoadApp.OrderStatus.${orderStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                id="order-customer"
                name="customer"
                data-cy="customer"
                label={translate('silkRoadApp.order.customer')}
                type="select"
                required
              >
                <option value="" key="0" />
                {customers
                  ? customers.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.email}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/order" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default OrderUpdate;
