import React, {useEffect, useState} from 'react'
import classNames from 'classnames/bind';

import styles from './guavaVersus.module.scss';
import {Button, Tag} from 'antd-mobile';
import {useRecoilValue, useRecoilState} from 'recoil';
import {buildingState, regionState, showVersusSearchState, versusRegionListState} from '../datatool/state';
import PlusOutlined from '@ant-design/icons/es/icons/PlusOutlined';
import CloseOutlined from '@ant-design/icons/es/icons/CloseOutlined';
import MinusCircleOutlined from '@ant-design/icons/es/icons/MinusCircleOutlined';
import GuavaVersusSearch from './GuavaVersusSearch';
import GuavaVersusTable from './GuavaVersusTable';
import GuavaVersusChart from './GuavaVersusChart';
import {getDetail} from '../datatool/api';
import GuavaTradeOption from '../detail/GuavaTradeOption';
import GuavaAreaTypeFilter from '../common/GuavaAreaTypeFilter';
import GuavaVersusTradeOption from './GuavaVersusTradeOption';

const cx = classNames.bind(styles);

const GuavaVersus = () => {
    const region = useRecoilValue(regionState);
    const [building, setBuilding] = useRecoilState(buildingState);
    const [versusRegionList, setVersusRegionList] = useRecoilState(versusRegionListState);
    const [showVersusSearch, setShowVersusSearch] = useRecoilState(showVersusSearchState);

    useEffect(() => {
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
    }, []);

    return (
        <>
            {/*<GuavaAreaTypeFilter/>*/}
            <div className={cx('versus_container')}>
                <GuavaVersusChart/>
                <GuavaVersusTable/>
            </div>
        </>
    )
};


export default GuavaVersus;