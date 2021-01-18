import React from 'react';
import GuavaChart from '../components/detail/GuavaChart';
import GuavaTable from '../components/table/GuavaTable';
import GuavaMarketChart from '../components/detail/GuavaMarketChart';
import GuavaMarketTable from '../components/table/GuavaMarketTable';

const DetailTradePage = () => {
    return (
        <>
            <GuavaChart/>
            <GuavaTable/>
            <GuavaMarketChart/>
            <GuavaMarketTable/>
        </>
    );
};

export default DetailTradePage;