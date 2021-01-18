import React, {useState} from 'react';
import GuavaDetailHeader from '../components/header/GuavaDetailHeader';
import GuavaBuildingInfo from '../components/detail/GuavaBuildingInfo';
import GuavaTradeOption from '../components/detail/GuavaTradeOption';
import GuavaMarketChart from '../components/detail/GuavaMarketChart';
import GuavaMarketTable from '../components/table/GuavaMarketTable';
import GuavaChart from '../components/detail/GuavaChart';
import GuavaTable from '../components/table/GuavaTable';

const DetailHeaderPage = ({match, location}) => {
    return (
        <>
            <GuavaDetailHeader tabId={!match.params.tabId ? '0' : match.params.tabId}/>
            <GuavaBuildingInfo/>
            <GuavaTradeOption/>
            {
                (!match.params.tabId || match.params.tabId === '0') &&
                <>
                    <GuavaChart/>
                    <GuavaTable/>
                </>
            }
            {
                match.params.tabId === '1' &&
                <>
                    <GuavaMarketChart/>
                    <GuavaMarketTable/>
                </>
            }
        </>
    );
};

export default DetailHeaderPage;