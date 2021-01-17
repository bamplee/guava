import React, {useEffect} from 'react'

import {useHistory, useLocation} from 'react-router-dom';

import classNames from 'classnames/bind';

import styles from './guavaHeader.module.scss';
import {useRecoilState, useRecoilValue} from 'recoil';
import {
    areaTypeState, buildingState,
    filterAreaState,
    regionState,
    showAreaFilterState,
    showAreaTypeFilterState, tableOptionState, tradeTypeState
} from '../datatool/state';
import ArrowLeftOutlined from '@ant-design/icons/es/icons/ArrowLeftOutlined';
import SearchOutlined from '@ant-design/icons/es/icons/SearchOutlined';
import {Button, WingBlank, Modal} from 'antd-mobile';
import CaretDownOutlined from '@ant-design/icons/es/icons/CaretDownOutlined';
import {getEndArea, getStartArea, TABLE_OPTION} from '../constant';
import ReactGA from 'react-ga';
import SendOutlined from '@ant-design/icons/es/icons/SendOutlined';

const cx = classNames.bind(styles);

const GuavaDetailHeader = () => {
    const location = useLocation();
    const history = useHistory();
    // const [region, setRegion] = useRecoilState(regionState);
    const region = useRecoilValue(regionState);
    const [showAreaFilter, setShowAreaFilter] = useRecoilState(showAreaFilterState);
    const [filterArea, setFilterArea] = useRecoilState(filterAreaState);
    const [areaType, setAreaType] = useRecoilState(areaTypeState);
    const [showAreaTypeFilter, setShowAreaTypeFilter] = useRecoilState(showAreaTypeFilterState);
    const building = useRecoilValue(buildingState);
    const [tableOption, setTableOption] = useRecoilState(tableOptionState);
    const [tradeType, setTradeType] = useRecoilState(tradeTypeState);

    return (
        <>
            <div className={cx('header_container')}>
                <div className={cx('title_container')}>
                    <WingBlank>
                        <div className={cx('left')} onClick={() => history.push('/')}>
                            <ArrowLeftOutlined/>
                        </div>
                    </WingBlank>
                    <div className={cx('center')} onClick={() => history.push(location.pathname + '/search')}>
                        <span className={cx('title')}>{region.name}</span>
                    </div>
                    <WingBlank>
                        <div className={cx('right')}>
                            {/*<SendOutlined style={{marginRight: 15}} onClick={() => Modal.prompt(region ? region.name : '', '수정이 필요한 내용을 입력하세요', [*/}
                            {/*    {text: '닫기'},*/}
                            {/*    {*/}
                            {/*        text: '전송', onPress: value => {*/}
                            {/*            ReactGA.ga('send', 'event', 'issues', (region ? region.name : '-'), value);*/}
                            {/*        }*/}
                            {/*    },*/}
                            {/*], 'default', '')}/>*/}
                            <SearchOutlined onClick={() => history.push(location.pathname + '/search')}/>
                        </div>
                    </WingBlank>
                </div>
                <div className={cx('filter_container')}>
                    <div className={cx('filter_select')}>
                        <Button
                            className={cx('filter_btn', tableOption === TABLE_OPTION.TRADE && 'filter_btn_active')}
                            type={'primary'}
                            onClick={() => setTableOption(TABLE_OPTION.TRADE)}
                            size={'small'}>시세</Button>
                    </div>
                    <div className={cx('filter_select')}>
                        <Button className={cx('filter_btn', tableOption === TABLE_OPTION.MARKET && 'filter_btn_active')}
                                type={'primary'} size={'small'}
                                onClick={() => setTableOption(TABLE_OPTION.MARKET)}
                        >매물</Button>
                    </div>
                    <div className={cx('filter_select')}>
                        <Button className={cx('filter_btn', areaType.areaId !== '' && 'filter_btn_active')}
                                type={'primary'} size={'small'}
                                inline
                                onClick={() => alert('비교')}>
                            <span>비교</span>
                        </Button>
                    </div>
                </div>
            </div>
        </>
    )
};

export default GuavaDetailHeader;