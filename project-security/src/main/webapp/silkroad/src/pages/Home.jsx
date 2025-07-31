// src/pages/Home.jsx
import React from "react";
import {
  Container,
  Typography,
  Grid,
  Box,
  Button,
  Card,
  CardContent,
  CardMedia,
} from "@mui/material";
import { useNavigate } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { productService } from "../services/productService";
import ProductCard from "../components/Product/ProductCard";
import LoadingSpinner from "../components/UI/LoadingSpinner";

const Home = () => {
  const navigate = useNavigate();

  // Prodotti in evidenza
  const { data: featuredProducts, isLoading } = useQuery({
    queryKey: ["featuredProducts"],
    queryFn: () => productService.getAllProducts({ page: 0, size: 8 }),
  });

  return (
    <Container maxWidth="lg">
      {/* Hero Section */}
      <Box
        sx={{
          background: "linear-gradient(45deg, #FE6B8B 30%, #FF8E53 90%)",
          color: "white",
          py: 8,
          px: 4,
          borderRadius: 2,
          textAlign: "center",
          mb: 6,
        }}
      >
        <Typography variant="h2" component="h1" gutterBottom>
          Benvenuto su SilkRoad
        </Typography>
        <Typography variant="h5" component="h2" gutterBottom>
          Il tuo marketplace di fiducia per shopping online
        </Typography>
        <Button
          variant="contained"
          size="large"
          sx={{ mt: 3, backgroundColor: "white", color: "primary.main" }}
          onClick={() => navigate("/products")}
        >
          Esplora Prodotti
        </Button>
      </Box>

      {/* Categorie Principali */}
      <Box sx={{ mb: 6 }}>
        <Typography variant="h4" component="h2" gutterBottom align="center">
          Categorie Principali
        </Typography>
        <Grid container spacing={3} sx={{ mt: 2 }}>
          {[
            {
              name: "Elettronica",
              image: "/images/electronics.jpg",
              categoryId: 1,
            },
            {
              name: "Abbigliamento",
              image: "/images/clothing.jpg",
              categoryId: 2,
            },
            {
              name: "Casa e Giardino",
              image: "/images/home.jpg",
              categoryId: 3,
            },
            { name: "Sport", image: "/images/sports.jpg", categoryId: 4 },
          ].map((category) => (
            <Grid item xs={12} sm={6} md={3} key={category.name}>
              <Card
                sx={{
                  cursor: "pointer",
                  "&:hover": { transform: "translateY(-5px)" },
                  transition: "transform 0.3s",
                }}
                onClick={() =>
                  navigate(`/products?category=${category.categoryId}`)
                }
              >
                <CardMedia
                  component="img"
                  height="200"
                  image={category.image}
                  alt={category.name}
                  sx={{ objectFit: "cover" }}
                />
                <CardContent>
                  <Typography variant="h6" align="center">
                    {category.name}
                  </Typography>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      </Box>

      {/* Prodotti in Evidenza */}
      <Box>
        <Typography variant="h4" component="h2" gutterBottom align="center">
          Prodotti in Evidenza
        </Typography>

        {isLoading ? (
          <LoadingSpinner />
        ) : (
          <Grid container spacing={3} sx={{ mt: 2 }}>
            {featuredProducts?.content?.slice(0, 8).map((product) => (
              <Grid item xs={12} sm={6} md={4} lg={3} key={product.id}>
                <ProductCard product={product} />
              </Grid>
            ))}
          </Grid>
        )}

        <Box sx={{ textAlign: "center", mt: 4 }}>
          <Button
            variant="outlined"
            size="large"
            onClick={() => navigate("/products")}
          >
            Vedi Tutti i Prodotti
          </Button>
        </Box>
      </Box>
    </Container>
  );
};

export default Home;
