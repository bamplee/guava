import React from 'react';
import GuavaDetailHeader from '../components/header/GuavaDetailHeader';
import GuavaBuildingInfo from '../components/detail/GuavaBuildingInfo';
import GuavaTradeOption from '../components/detail/GuavaTradeOption';
import GuavaMarketChart from '../components/detail/GuavaMarketChart';
import GuavaMarketTable from '../components/table/GuavaMarketTable';
import GuavaChart from '../components/detail/GuavaChart';
import GuavaTable from '../components/table/GuavaTable';
import {useRecoilValue} from 'recoil';
import {regionState} from '../components/datatool/state';
import GuavaAreaTypeFilter from '../components/common/GuavaAreaTypeFilter';

const DetailHeaderPage = ({match, location}) => {
    const region = useRecoilValue(regionState);

    return (
        <>
            <GuavaDetailHeader tabId={!match.params.tabId ? 't' : match.params.tabId}/>
            {
                region && match.params.tabId === 'i' &&
                <GuavaBuildingInfo/>
            }
            {
                region && (!match.params.tabId || match.params.tabId === 't') &&
                <>
                    <GuavaAreaTypeFilter/>
                    <GuavaTradeOption/>
                    <GuavaChart/>
                    <GuavaTable/>
                </>
            }
            {
                region && match.params.tabId === 'm' &&
                <>
                    <GuavaAreaTypeFilter/>
                    <GuavaTradeOption/>
                    <GuavaMarketChart/>
                    <GuavaMarketTable/>
                </>
            }
            {
                region && match.params.tabId === 'c' &&
                <GuavaBuildingInfo/>
            }
        </>
    );
};

export default DetailHeaderPage;