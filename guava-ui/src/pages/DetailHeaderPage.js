import React from 'react';
import GuavaDetailHeader from '../components/header/GuavaDetailHeader';
import GuavaBuildingInfo from '../components/detail/GuavaBuildingInfo';
import GuavaTradeOption from '../components/detail/GuavaTradeOption';
import GuavaMarketChart from '../components/detail/GuavaMarketChart';
import GuavaMarketTable from '../components/table/GuavaMarketTable';
import GuavaChart from '../components/detail/GuavaChart';
import GuavaTable from '../components/table/GuavaTable';
import {useRecoilState, useRecoilValue} from 'recoil';
import {regionState, showVersusSearchState} from '../components/datatool/state';
import GuavaAreaTypeFilter from '../components/common/GuavaAreaTypeFilter';
import GuavaVersus from '../components/versus/GuavaVersus';
import GuavaDetailTabs from '../components/header/GuavaDetailTabs';
import GuavaVersusTradeOption from '../components/versus/GuavaVersusTradeOption';
import GuavaVersusSearch from '../components/versus/GuavaVersusSearch';
import {Button} from 'antd-mobile';
import PlusOutlined from '@ant-design/icons/es/icons/PlusOutlined';
import GuavaVersusButton from '../components/versus/GuavaVersusButton';

const DetailHeaderPage = ({match, location}) => {
    const region = useRecoilValue(regionState);
    const [showVersusSearch, setShowVersusSearch] = useRecoilState(showVersusSearchState);

    return (
        !showVersusSearch ?
            <>
                <GuavaAreaTypeFilter/>
                <GuavaDetailHeader tabId={!match.params.tabId ? 't' : match.params.tabId}/>
                <GuavaBuildingInfo/>
                {/*<GuavaDetailTabs tabId={!match.params.tabId ? 't' : match.params.tabId}/>*/}
                <div style={{maxWidth: 500, margin: '0 auto'}}>
                    {
                        (region && (region.type === 'BUILDING' ? region.buildingId === match.params.regionId : region.id === match.params.regionId)) &&
                        <>
                            {/*{*/}
                            {/*    match.params.tabId === 'i' &&*/}
                            {/*    <GuavaBuildingInfo/>*/}
                            {/*}*/}
                            {
                                (!match.params.tabId || match.params.tabId === 't') &&
                                <>
                                    {/*<GuavaTradeOption/>*/}
                                    <GuavaTradeOption type={'BUILDING'}/>
                                    <GuavaChart/>
                                    <GuavaTable/>
                                </>
                            }
                            {
                                match.params.tabId === 'm' &&
                                <>
                                    <GuavaTradeOption type={'BUILDING'}/>
                                    <GuavaMarketChart/>
                                    <GuavaMarketTable/>
                                </>
                            }
                            {
                                match.params.tabId === 'c' &&
                                <>
                                    <GuavaTradeOption type={'REGION'}/>
                                    <GuavaVersus/>
                                    <GuavaVersusButton/>
                                </>
                            }
                        </>
                    }
                </div>
            </> :
            <GuavaVersusSearch/>
    );
};

export default DetailHeaderPage;