import React, {useEffect} from 'react'
import classNames from 'classnames/bind';

import styles from './guavaVersus.module.scss';
import {Button, Tag} from 'antd-mobile';
import {useRecoilValue, useRecoilState} from 'recoil';
import {regionState, showVersusSearchState, versusRegionListState} from '../datatool/state';
import PlusOutlined from '@ant-design/icons/es/icons/PlusOutlined';
import CloseOutlined from '@ant-design/icons/es/icons/CloseOutlined';
import MinusCircleOutlined from '@ant-design/icons/es/icons/MinusCircleOutlined';
import GuavaVersusSearch from './GuavaVersusSearch';

const cx = classNames.bind(styles);

const GuavaVersus = () => {
    const region = useRecoilValue(regionState);
    const [versusRegionList, setVersusRegionList] = useRecoilState(versusRegionListState);
    const [showVersusSearch, setShowVersusSearch] = useRecoilState(showVersusSearchState);

    const removeVersusRegion = (region) => {
        let temp = [...versusRegionList];
        temp = temp.filter(x => !(x.type === 'BUILDING' ? x.buildingId === region.buildingId : x.id === region.id));
        setVersusRegionList([...temp]);
    };

    return (
        <div className={cx('versus_container')}>
            <GuavaVersusSearch/>
            <div className={cx('tag_list')}>
                {/*<Button className={cx('tag')} inline ghost size="small">{region.name}</Button>*/}
                {
                    versusRegionList.map(x => (
                        <Button type={'primary'} className={cx('tag')} inline ghost
                                size="small">{x.name}<CloseOutlined style={{marginLeft: 5, fontSize: 12}}
                                                                    onClick={() => removeVersusRegion(x)}/></Button>
                    ))
                }
            </div>
            <Button className={cx('tag_add')} type="default" size="small"
                    onClick={() => setShowVersusSearch(true)}><PlusOutlined/> 추가하기</Button>
        </div>
    )
};


export default GuavaVersus;