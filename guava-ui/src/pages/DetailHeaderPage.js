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
            <GuavaDetailHeader tabId={!match.params.tabId ? '0' : match.params.tabId}/>
            {
                region &&
                (match.params.tabId === 'info' ?
                        <div>
                            <GuavaBuildingInfo/>
                        </div>
                        :
                        <>
                            <GuavaAreaTypeFilter/>
                            {
                                (!match.params.tabId || match.params.tabId === '0') &&
                                <>
                                    <GuavaTradeOption/>
                                    <GuavaChart/>
                                    <GuavaTable/>
                                </>
                            }
                            {
                                match.params.tabId === '1' &&
                                <>
                                    <GuavaTradeOption/>
                                    <GuavaMarketChart/>
                                    <GuavaMarketTable/>
                                </>
                            }
                        </>
                )
            }
        </>
    );
};

export default DetailHeaderPage;