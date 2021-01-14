import React, {useEffect} from 'react'
import {useParams} from 'react-router-dom';

import classNames from 'classnames/bind';
import GuavaBuildingInfo from './GuavaBuildingInfo';
import GuavaHeader from '../header/GuavaHeader';
import GuavaTradeOption from './GuavaTradeOption';
import GuavaChart from './GuavaChart';
import GuavaTable from '../table/GuavaTable';

import styles from './guavaDetailPage.module.scss';
import {useRecoilValue, useRecoilState} from 'recoil';
import {
    buildingState, regionState, tableOptionState
} from '../datatool/state';
import {WhiteSpace} from 'antd-mobile';
import GuavaMarketChart from './GuavaMarketChart';
import {getBuilding, getDetail, getRegion} from '../datatool/api';

const cx = classNames.bind(styles);

const GuavaDetailPage = () => {
    const {buildingId, regionId} = useParams();
    const [region, setRegion] = useRecoilState(regionState);
    const [building, setBuilding] = useRecoilState(buildingState);
    const tableOption = useRecoilValue(tableOptionState);

    useEffect(() => {
        if (buildingId) {
            const init = async () => {
                setRegion(await getBuilding(buildingId));
                setBuilding(await getDetail(buildingId));
            };
            init();
        }
    }, [buildingId]);

    useEffect(() => {
        if (regionId) {
            const init = async () => {
                setRegion(await getRegion(regionId));
                setBuilding(null);
            };
            init();
        }
    }, [regionId]);

    return (
        <>
            {
                ((region.buildingId === buildingId)
                    || (region.id === regionId)) &&
                <div style={{paddingTop: 90}}>
                    <GuavaHeader/>
                    <GuavaBuildingInfo/>
                    {/*<WhiteSpace/>*/}
                    <GuavaTradeOption/>
                    {
                        tableOption === 'trade' ?
                            <GuavaChart/> :
                            <GuavaMarketChart/>
                    }
                    <GuavaTable/>
                </div>
            }
        </>
    );
};

export default GuavaDetailPage;