import React, {useEffect} from 'react';
import {useRecoilState, useRecoilValue} from 'recoil';
import {
    areaTypeState,
    buildingState,
    regionState,
    showVersusSearchState,
    versusRegionListState
} from '../../datatool/state';
import classNames from "classnames/bind";

import styles from '../../../pages/guavaDetailPage.module.scss';
import Header from "./header/Header";
import {getBuilding, getDetail, getRegion} from "../../datatool/api";
import {useHistory, useParams} from "react-router-dom";
import Summary from "./summary/Summary";
import TradeChart from "./TradeChart";
import TradeList from "./TradeList";
import MarketList from "./MarketList";
import MarketChart from "./MarketChart";
import Line from "./Line";
import VersusChart from "./VersusChart";
import VersusList from "./VersusList";
import TradeTabs from "./TradeTabs";

const cx = classNames.bind(styles);

const Detail = () => {
    const {tabId} = useParams();
    const [areaType, setAreaType] = useRecoilState(areaTypeState);
    const {regionType, buildingId, regionId} = useParams();
    const [region, setRegion] = useRecoilState(regionState);
    const [versusRegionList, setVersusRegionList] = useRecoilState(versusRegionListState);
    const [building, setBuilding] = useRecoilState(buildingState);

    useEffect(() => {
        setRegion(null);
        setAreaType({areaId: ''});

        const init = async () => {
            if (region) {
                if (region.type === 'BUILDING') {
                    let building = await getDetail(region.buildingId);
                    setBuilding(building);
                    if (versusRegionList.length === 0) {
                        setVersusRegionList([building]);
                    }
                } else {
                    if (versusRegionList.length === 0) {
                        setVersusRegionList([region]);
                    }
                }
            }
        };
        init();

        initRegion();
    }, []);

    const initRegion = async () => {
        if (regionType === 'b') {
            setRegion(await getBuilding(regionId));
        } else {
            setRegion(await getRegion(regionId));
        }
    };

    return (
        <>
            <Header/>
            {/*<Option/>*/}
            <Summary/>
            <Line/>
            {
                region &&
                <>
                    <div className={cx('p-3 pl-5 text-black text-lg font-bold')}>
                        실거래가
                    </div>
                    <TradeTabs/>
                    {
                        tabId === 'c' ?
                            <>
                                <VersusChart/>
                                <VersusList/>
                            </> :
                            <>
                                <TradeChart/>
                                <TradeList/>
                            </>
                    }
                    <Line/>
                    <div className={cx('p-3 pl-5 text-black text-lg font-bold')}>
                        호가
                    </div>
                    <MarketChart/>
                    <MarketList/>
                </>
            }
            <Line/>
            <div className={cx('flex content-center justify-center text-gray-400 p-4')}>
                <div>© 2021. 구아바맵</div>
            </div>
        </>
    );
};

export default Detail;