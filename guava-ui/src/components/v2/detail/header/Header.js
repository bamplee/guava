import React, {useEffect, useState} from 'react'

import {useHistory} from 'react-router-dom';

import classNames from 'classnames/bind';

import styles from './header.module.scss';
import {useRecoilState, useRecoilValue} from 'recoil';
import {
    areaTypeState,
    filterAreaState,
    regionState, showAreaFilterState,
    showAreaTypeFilterState, showDetailSummaryState,
    tradeTypeState
} from '../../../datatool/state';
import ArrowLeftOutlined from '@ant-design/icons/es/icons/ArrowLeftOutlined';
import CaretDownOutlined from '@ant-design/icons/es/icons/CaretDownOutlined';
import {Button, WingBlank} from 'antd-mobile';
import operation from "antd-mobile/es/modal/operation";
import AreaRangeModal from "../option/AreaRangeModal";
import AreaTypeModal from "../option/AreaTypeModal";
import {CloseOutlined} from "@ant-design/icons";
import {getEndArea, getStartArea} from "../../../constant";
import {getDetail} from "../../../datatool/api";

const cx = classNames.bind(styles);

const Header = () => {
    const history = useHistory();
    const [region] = useRecoilState(regionState);
    const [tradeType, setTradeType] = useRecoilState(tradeTypeState);
    const [areaType, setAreaType] = useRecoilState(areaTypeState);
    const [showAreaTypeFilter, setShowAreaTypeFilter] = useRecoilState(showAreaTypeFilterState);
    const [showAreaFilter, setShowAreaFilter] = useRecoilState(showAreaFilterState);
    const [filterArea, setFilterArea] = useRecoilState(filterAreaState);
    const [showDetailSummary, setShowDetailSummary] = useRecoilState(showDetailSummaryState);
    const [building, setBuilding] = useState(null);

    useEffect(() => {
        if (region) {
            initSummary();
        }
    }, [region]);

    const initSummary = async () => {
        if (region.type === 'BUILDING') {
            setBuilding(await getDetail(region.buildingId));
        }
    }

    const getTradeName = () => {
        return tradeType === 'trade' ? '매매' : '전/월세'
    }

    return (
        <>
            <div className={cx('w-full fixed z-99999')}>
                <div className={cx('flex justify-between h-12 p-3 bg-guava')}>
                    {/*<div className={cx('left')} onClick={() => history.push('/')}>*/}
                    {/*    <ArrowLeftOutlined/>*/}
                    {/*</div>*/}
                    <div onClick={() => history.push('/')}
                         className={cx('flex items-center flex-none text-center text-white transform hover:scale-110 motion-reduce:transform-none cursor-pointer')}>
                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"
                             xmlns="http://www.w3.org/2000/svg">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                  d="M10 19l-7-7m0 0l7-7m-7 7h18"/>
                        </svg>
                    </div>
                    <div
                        className={cx('flex items-center justify-center flex-grow text-white text-base font-medium')}
                        onClick={() => setShowDetailSummary(!showDetailSummary)}>
                        {region?.name}
                    </div>
                    <div className={cx('flex items-center flex-none text-center text-white')}
                         onClick={() => setShowDetailSummary(!showDetailSummary)}>
                        {
                            showDetailSummary ?
                                <svg className="w-5 h-5" xmlns="http://www.w3.org/2000/svg" fill="none"
                                     viewBox="0 0 24 24" stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                          d="M5 15l7-7 7 7"/>
                                </svg> :
                                <svg className="w-5 h-5" xmlns="http://www.w3.org/2000/svg" fill="none"
                                     viewBox="0 0 24 24"
                                     stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                          d="M19 9l-7 7-7-7"/>
                                </svg>
                        }
                    </div>
                </div>
                {
                    showDetailSummary &&
                    <div
                        className={cx('pt-2 pb-2 bg-guava w-full border border-guava-light border-r-0 text-white font-medium')}>
                        <div className={cx('text-center text-white pb-1')}>{region?.address}</div>
                        {
                            building &&
                            <>
                                <div className={cx('flex justify-center text-xs')}>
                                    <div className={cx('text-gray-200')}>{building.since}</div>
                                    <div className={cx('text-guava-light ml-1 mr-1')}>|</div>
                                    <div className={cx('text-gray-200')}>용적률 {building.floorAreaRatio}%</div>
                                    <div className={cx('text-guava-light ml-1 mr-1')}>|</div>
                                    <div className={cx('text-gray-200')}>건폐율 {building.buildingCoverageRatio}%</div>
                                </div>
                                <div className={cx('flex justify-center text-xs')}>
                                    <div className={cx('text-gray-200')}>{building.buildingCount}개동</div>
                                    <div className={cx('text-guava-light ml-1 mr-1')}>|</div>
                                    <div className={cx('text-gray-200')}>{building.minFloor}~{building.maxFloor}층</div>
                                    <div className={cx('text-guava-light ml-1 mr-1')}>|</div>
                                    <div className={cx('text-gray-200')}>{building.hoCount}세대</div>
                                    <div className={cx('text-guava-light ml-1 mr-1')}>|</div>
                                    <div className={cx('text-gray-200')}>주차 {building.parkingTotal}대</div>
                                </div>
                            </>
                        }
                    </div>
                }
                <div className={cx('flex justify-between h-10 bg-guava')}>
                    <div
                        className={cx('flex justify-center items-center w-5/12 border border-guava-light border-r-0 text-white text-sm font-medium')}
                        onClick={() => operation([
                            {text: '매매', onPress: () => setTradeType('trade')},
                            {text: '전/월세', onPress: () => setTradeType('rent')},
                        ])}>
                        <span className={cx('mr-1')}>{getTradeName()}</span>
                        <svg className="w-3 h-3" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"
                             stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/>
                        </svg>
                    </div>
                    {
                        region?.type === 'BUILDING' ?
                            <div
                                className={cx('flex justify-center items-center w-5/12 border border-guava-light border-r-0 text-white text-sm font-medium')}
                                onClick={() => setShowAreaTypeFilter(true)}>
                                <span className={cx('mr-1')}>{areaType.type ? areaType.type : '평형'}</span>
                                <svg className="w-3 h-3" xmlns="http://www.w3.org/2000/svg" fill="none"
                                     viewBox="0 0 24 24" stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                          d="M19 9l-7 7-7-7"/>
                                </svg>
                            </div> :
                            <div
                                className={cx('flex justify-center items-center w-4/12 border border-guava-light border-r-0 text-white text-sm font-medium')}
                                onClick={() => setShowAreaFilter(true)}>
                                <span
                                    className={cx('mr-1')}>{filterArea[0] === 0 && filterArea[1] === 5 ? '면적' : `${getStartArea(filterArea[0])}㎡~${getEndArea(filterArea[1])}㎡`}
                                </span>
                                <svg className="w-3 h-3" xmlns="http://www.w3.org/2000/svg" fill="none"
                                     viewBox="0 0 24 24" stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                          d="M19 9l-7 7-7-7"/>
                                </svg>
                            </div>
                    }
                    <div
                        className={cx('flex justify-center items-center w-5/12 border border-guava-light border-r-0 text-white text-sm font-medium')}>
                        <svg className="w-5 h-5 text-gray-400" xmlns="http://www.w3.org/2000/svg" fill="none"
                             viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18 12H6"/>
                        </svg>
                    </div>
                    <div
                        className={cx('flex justify-center items-center w-2/12 border border-guava-light border-r-0 text-white text-sm font-medium')}>
                        <div className={cx('flex justify-center items-center h-full w-full')}>
                            {/*<svg className="w-5 h-5" fill="none" stroke="currentColor" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">*/}
                            {/*    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />*/}
                            {/*</svg>*/}
                            <svg className="w-5 h-5 text-gray-400" xmlns="http://www.w3.org/2000/svg" fill="none"
                                 viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18 12H6"/>
                            </svg>
                        </div>
                    </div>
                    <div
                        className={cx('flex justify-center items-center w-2/12 border border-guava-light border-r-0 text-white text-sm font-medium')}>
                        <div className={cx('flex justify-center items-center h-full w-full')}>
                            {/*<svg className="w-5 h-5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">*/}
                            {/*    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 5a2 2 0 012-2h10a2 2 0 012 2v16l-7-3.5L5 21V5z" />*/}
                            {/*</svg>*/}
                            <svg className="w-5 h-5 text-gray-400" xmlns="http://www.w3.org/2000/svg" fill="none"
                                 viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18 12H6"/>
                            </svg>
                        </div>
                    </div>
                </div>
            </div>
            <div style={{height: showDetailSummary? 163 : 88}}/>
        </>
    )
};

export default Header;