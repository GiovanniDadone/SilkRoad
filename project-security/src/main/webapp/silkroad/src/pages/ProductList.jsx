// src/pages/ProductList.jsx
import React, { useState, useEffect } from 'react';
import {
  Container,
  Grid,
  Typography,
  Box,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Pagination,
  Paper,
  Slider,
  Button,
  Chip,
} from '@mui/material';
import { useSearchParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { productService } from '../services/productService';
import { categoryService } from '../services/categoryService';
import ProductCard from '../components/Product/ProductCard';
import LoadingSpinner from '../components/UI/LoadingSpinner';

const ProductList = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const [filters, setFilters] = useState({
    page: 0,
    size: 12,
    sortBy: 'name',
    sortDirection: 'ASC',
    categoryId: null,
    minPrice: 0,
    maxPrice: 1000,
    keyword: searchParams.get('search') || '',
  });

  // Query per prodotti
  const { data: productsData, isLoading, error } = useQuery({
    queryKey: ['products', filters],
    queryFn: () => {
      if (filters.keyword) {
        return productService.searchProducts(filters.keyword, {
          page: filters.page,
          size: filters.size,
        });
      } else if (filters.categoryId) {
        return productService.getProductsByCategory(filters.categoryId, {
          page: filters.page,
          size: filters.size,
        });
      } else {
        return productService.filterProducts(filters);
      }
    },
  });

  // Query per categorie
  const { data: categories } = useQuery({
    queryKey: ['categories'],
    queryFn: () => categoryService.getAllCategories(),
  });

  useEffect(() => {
    const search = searchParams.get('search');
    const category = searchParams.get('category');
    
    if (search) {
      setFilters(prev => ({ ...prev, keyword: search, categoryId: null }));
    }
    if (category) {
      setFilters(prev => ({ ...prev, categoryId: parseInt(category), keyword: '' }));
    }
  }, [searchParams]);

  const handleFilterChange = (field, value) => {
    setFilters(prev => ({
      ...prev,
      [field]: value,
      page: 0, // Reset page when filter changes
    }));
  };

  const handlePageChange = (event, value) => {
    setFilters(prev => ({ ...prev, page: value - 1 }));
  };

  const clearFilters = () => {
    setFilters({
      page: 0,
      size: 12,
      sortBy: 'name',
      sortDirection: 'ASC',
      categoryId: null,
      minPrice: 0,
      maxPrice: 1000,
      keyword: '',
    });
    setSearchParams({});
  };

  return (
    <Container maxWidth="lg">
      <Typography variant="h4" component="h1" gutterBottom>
        Catalogo Prodotti
      </Typography>

      <Grid container spacing={3}>
        {/* Sidebar Filtri */}
        <Grid item xs={12} md={3}>
          <Paper sx={{ p: 3, sticky: 'top' }}>
            <Typography variant="h6" gutterBottom>
              Filtri
            </Typography>

            {/* Ricerca */}
            <TextField
              fullWidth
              label="Cerca prodotti"
              value={filters.keyword}
              onChange={(e) => handleFilterChange('keyword', e.target.value)}
              sx={{ mb: 3 }}
            />

            {/* Categoria */}
            <FormControl fullWidth sx={{ mb: 3 }}>
              <InputLabel>Categoria</InputLabel>
              <Select
                value={filters.categoryId || ''}
                label="Categoria"
                onChange={(e) => handleFilterChange('categoryId', e.target.value || null)}
              >
                <MenuItem value="">Tutte</MenuItem>
                {categories?.map((category) => (
                  <MenuItem key={category.id} value={category.id}>
                    {category.name}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>

            {/* Range Prezzo */}
            <Typography gutterBottom>
              Prezzo: €{filters.minPrice} - €{filters.maxPrice}
            </Typography>
            <Slider
              value={[filters.minPrice, filters.maxPrice]}
              onChange={(e, newValue) => {
                handleFilterChange('minPrice', newValue[0]);
                handleFilterChange('maxPrice', newValue[1]);
              }}
              valueLabelDisplay="auto"
              min={0}
              max={1000}
              sx={{ mb: 3 }}
            />

            {/* Ordinamento */}
            <FormControl fullWidth sx={{ mb: 3 }}>
              <InputLabel>Ordina per</InputLabel>
              <Select
                value={`${filters.sortBy}-${filters.sortDirection}`}
                label="Ordina per"
                onChange={(e) => {
                  const [sortBy, sortDirection] = e.target.value.split('-');
                  handleFilterChange('sortBy', sortBy);
                  handleFilterChange('sortDirection', sortDirection);
                }}
              >
                <MenuItem value="name-ASC">Nome A-Z</MenuItem>
                <MenuItem value="name-DESC">Nome Z-A</MenuItem>
                <MenuItem value="price-ASC">Prezzo: Basso → Alto</MenuItem>
                <MenuItem value="price-DESC">Prezzo: Alto → Basso</MenuItem>
              </Select>
            </FormControl>

            <Button
              fullWidth
              variant="outlined"
              onClick={clearFilters}
            >
              Cancella Filtri
            </Button>
          </Paper>
        </Grid>

        {/* Lista Prodotti */}
        <Grid item xs={12} md={9}>
          {/* Filtri Attivi */}
          <Box sx={{ mb: 2, display: 'flex', flexWrap: 'wrap', gap: 1 }}>
            {filters.keyword && (
              <Chip
                label={`Ricerca: "${filters.keyword}"`}
                onDelete={() => handleFilterChange('keyword', '')}
              />
            )}
            {filters.categoryId && (
              <Chip
                label={`Categoria: ${categories?.find(c => c.id === filters.categoryId)?.name}`}
                onDelete={() => handleFilterChange('categoryId', null)}
              />
            )}
          </Box>

          {/* Risultati */}
          {isLoading ? (
            <LoadingSpinner />
          ) : error ? (
            <Typography color="error" align="center">
              Errore nel caricamento dei prodotti
            </Typography>
          ) : productsData?.content?.length === 0 ? (
            <Typography align="center" sx={{ py: 4 }}>
              Nessun prodotto trovato con i filtri selezionati
            </Typography>
          ) : (
            <>
              <Typography variant="body2" sx={{ mb: 2 }}>
                {productsData?.totalElements} prodotti trovati
              </Typography>

              <Grid container spacing={3}>
                {productsData?.content?.map((product) => (
                  <Grid item xs={12} sm={6} lg={4} key={product.id}>
                    <ProductCard product={product} />
                  </Grid>
                ))}
              </Grid>

              {/* Paginazione */}
              {productsData?.totalPages > 1 && (
                <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
                  <Pagination
                    count={productsData.totalPages}
                    page={filters.page + 1}
                    onChange={handlePageChange}
                    color="primary"
                  />
                </Box>
              )}
            </>
          )}
        </Grid>
      </Grid>
    </Container>
  );
};

export default ProductList;