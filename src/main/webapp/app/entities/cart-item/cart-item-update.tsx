import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getCarts } from 'app/entities/cart/cart.reducer';
import { getEntities as getProducts } from 'app/entities/product/product.reducer';
import { createEntity, getEntity, reset, updateEntity } from './cart-item.reducer';

export const CartItemUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const carts = useAppSelector(state => state.cart.entities);
  const products = useAppSelector(state => state.product.entities);
  const cartItemEntity = useAppSelector(state => state.cartItem.entity);
  const loading = useAppSelector(state => state.cartItem.loading);
  const updating = useAppSelector(state => state.cartItem.updating);
  const updateSuccess = useAppSelector(state => state.cartItem.updateSuccess);

  const handleClose = () => {
    navigate(`/cart-item${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getCarts({}));
    dispatch(getProducts({}));
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
    if (values.quantity !== undefined && typeof values.quantity !== 'number') {
      values.quantity = Number(values.quantity);
    }

    const entity = {
      ...cartItemEntity,
      ...values,
      cart: carts.find(it => it.id.toString() === values.cart?.toString()),
      product: products.find(it => it.id.toString() === values.product?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...cartItemEntity,
          cart: cartItemEntity?.cart?.id,
          product: cartItemEntity?.product?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="silkRoadApp.cartItem.home.createOrEditLabel" data-cy="CartItemCreateUpdateHeading">
            <Translate contentKey="silkRoadApp.cartItem.home.createOrEditLabel">Create or edit a CartItem</Translate>
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
                  id="cart-item-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('silkRoadApp.cartItem.quantity')}
                id="cart-item-quantity"
                name="quantity"
                data-cy="quantity"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                id="cart-item-cart"
                name="cart"
                data-cy="cart"
                label={translate('silkRoadApp.cartItem.cart')}
                type="select"
                required
              >
                <option value="" key="0" />
                {carts
                  ? carts.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="cart-item-product"
                name="product"
                data-cy="product"
                label={translate('silkRoadApp.cartItem.product')}
                type="select"
                required
              >
                <option value="" key="0" />
                {products
                  ? products.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/cart-item" replace color="info">
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

export default CartItemUpdate;
