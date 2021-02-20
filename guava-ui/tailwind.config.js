const colors = require('tailwindcss/colors')

module.exports = {
    purge: ['./src/**/*.{js,jsx,ts,tsx}', './public/index.html'],
    darkMode: false, // or 'media' or 'class'
    theme: {
        colors: {
            transparent: 'transparent',
            current: 'currentColor',
            black: colors.black,
            white: colors.white,
            gray: colors.trueGray,
            indigo: colors.indigo,
            red: colors.rose,
            yellow: colors.amber,
            green: colors.green,
            guava: {
                light: '#288C07',
                DEFAULT: '#00802E',
                dark: '#006526'
            },
            guavaOrange: {
                light: '#D9890B',
                DEFAULT: '#CC5214',
                dark: '#D9130B'
            },
        },
        extend: {
            zIndex: {
                '99999': 99999,
            },
        },
    },
    variants: {
        extend: {},
    },
    plugins: [],
}