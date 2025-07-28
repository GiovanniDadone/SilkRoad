module.exports = {
    env: {
        browser: true,
        es2024: true,
    },
    extends: [
        "eslint:recommended",
        "plugin:react-hooks/recommended",
        "plugin:react-refresh/recommended",
        "prettier" // Aggiungi questa linea per disabilitare regole ESLint che confliggono con Prettier
    ],
    parserOptions: {
        ecmaVersion: "latest",
        sourceType: "module",
    },
    rules: {
        "react-refresh/only-export-components": "warn",
        "no-unused-vars": "warn" // Esempio: modifica le regole a tuo piacimento
    }
}