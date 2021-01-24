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
    const [versusRegionList, setVersusRegionList] = useState([]);
    const [showVersusSearch, setShowVersusSearch] = useRecoilState(showVersusSearchState);

    useEffect(() => {
        const init = async () => {
            if (region) {
                if (region.type === 'BUILDING') {
                    let building = await getDetail(region.buildingId);
                    setBuilding(building);
                    setVersusRegionList([building]);
                } else {
                    setVersusRegionList([region]);
                }
            }
        };
        init();
    }, []);

    return (
        <>
            <GuavaAreaTypeFilter/>
            <GuavaVersusSearch versusRegionList={versusRegionList} setVersusRegionList={setVersusRegionList}/>
            <div className={cx('versus_container')}>
                <GuavaVersusTradeOption/>
                <GuavaVersusChart versusRegionList={versusRegionList} setVersusRegionList={setVersusRegionList}/>
                <GuavaVersusTable versusRegionList={versusRegionList} setVersusRegionList={setVersusRegionList}/>
                <div className={cx('add_container')}>
                    <Button className={cx('tag_add')} inline type="ghost"
                            onClick={() => setShowVersusSearch(true)}><PlusOutlined
                        style={{marginRight: 5}}/>비교할 지역 / 아파트 추가하기</Button>
                </div>
            </div>
        </>
    )
};


export default GuavaVersus;