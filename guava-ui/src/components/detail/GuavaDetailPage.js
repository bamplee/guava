import React from 'react'
import {useParams} from 'react-router-dom';

import classNames from 'classnames/bind';
import GuavaBuildingInfo from './GuavaBuildingInfo';
import GuavaTradeOption from './GuavaTradeOption';
import GuavaChart from './GuavaChart';
import GuavaTable from '../table/GuavaTable';

import styles from './guavaDetailPage.module.scss';
import {useRecoilValue} from 'recoil';
import {regionState} from '../datatool/state';
import GuavaMarketChart from './GuavaMarketChart';

const cx = classNames.bind(styles);

const GuavaDetailPage = () => {
    const {buildingId, regionId} = useParams();
    const region = useRecoilValue(regionState);

    return (
        <>
            {
                ((region.buildingId === buildingId)
                    || (region.id === regionId)) &&
                <div style={{paddingTop: 90}}>
                    {/*<GuavaDetailHeader/>*/}
                    <GuavaBuildingInfo/>
                    {/*<WhiteSpace/>*/}
                    <GuavaTradeOption/>
                    <GuavaChart/>
                    <GuavaMarketChart/>
                    <GuavaTable/>
                </div>
            }
        </>
    );
};

export default GuavaDetailPage;