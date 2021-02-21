import React, {useEffect, useState} from 'react'
import {useHistory} from 'react-router-dom';
import {useRecoilState, useRecoilValue} from 'recoil';
import InfiniteScroll from 'react-infinite-scroll-component';

import {areaTypeState, filterAreaState, regionState, tradeDateState, tradeTypeState} from '../../datatool/state';
import {getRegionTradeMarket, getTradeMarket} from '../../datatool/api';

import classNames from 'classnames/bind';
import styles from './marketList.module.scss';
import {getEndArea, getStartArea} from '../../constant';
import Loading from './Loading';
import {Badge} from "antd-mobile";

const cx = classNames.bind(styles);

const INIT_ITEM_SIZE = 5;
const ITEM_SIZE = 30;

const MarketList = () => {
        const history = useHistory();
        const [page, setPage] = useState(0);
        const [isLoading, setIsLoading] = useState(false);
        const [isCompleted, setIsCompleted] = useState(false);
        const [tradeList, setTradeList] = useState([]);
        const [areaType, setAreaType] = useRecoilState(areaTypeState);
        const [tradeDate, setTradeDate] = useRecoilState(tradeDateState);
        // const [region, setRegion] = useRecoilState(regionState);
        const [filterArea, setFilterArea] = useRecoilState(filterAreaState);
        const region = useRecoilValue(regionState);
        // const [count, setCount] = useState(0);
        // const [chartList, setChartList] = useState([]);
        const [tradeType, setTradeType] = useRecoilState(tradeTypeState);
        const [isPreview, setIsPreview] = useState(true);

        useEffect(() => {
            initFetch();
        }, []);

        useEffect(() => {
            setPage(0);
            setTradeList([]);
            setIsLoading(false);
            setIsCompleted(false);
            // fetch();
            initFetch();
        }, [region, tradeType, areaType, filterArea, tradeDate]);

        const initFetch = async () => {
            if (!isCompleted && !isLoading) {
                setIsLoading(true);
                let date = '';
                if (tradeDate !== null) {
                    date = tradeDate.format('YYYYMM');
                }
                let startArea = getStartArea(filterArea[0]);
                let endArea = getEndArea(filterArea[1]);

                let result = [];
                if (region.type === 'BUILDING') {
                    result = await getTradeMarket(tradeType, region.buildingId, page, INIT_ITEM_SIZE, areaType.areaId, date);
                } else {
                    result = await getRegionTradeMarket(tradeType, region.id, page, INIT_ITEM_SIZE, startArea, endArea, date);
                }

                setTradeList(result);
                setIsLoading(false);
                if (result.length === 0) {
                    setIsCompleted(true);
                }
            }
        };

        const fetch = async () => {
            if (!isCompleted && !isLoading) {
                setIsLoading(true);
                let date = '';
                if (tradeDate !== null) {
                    date = tradeDate.format('YYYYMM');
                }
                let startArea = getStartArea(filterArea[0]);
                let endArea = getEndArea(filterArea[1]);

                let result = [];
                if (region.type === 'BUILDING') {
                    result = await getTradeMarket(tradeType, region.buildingId, page, ITEM_SIZE, areaType.areaId, date);
                } else {
                    result = await getRegionTradeMarket(tradeType, region.id, page, ITEM_SIZE, startArea, endArea, date);
                }

                if (page === 0) {
                    setTradeList(result);
                } else {
                    setTradeList(tradeList.concat(result));
                }

                setPage(page + 1);
                setIsLoading(false);
                if (result.length === 0) {
                    setIsCompleted(true);
                }

            }
        };

        const getRow = (page, index, trade) => {
            return (
                <tr groupKey={page} key={page + '-' + index}
                    className={cx('border-b text-gray-600 text-center text-xs', !trade.isActive && 'bg-gray-100 line-through')}>
                    <td className={cx('p-2')}>
                        <Badge dot={trade.isNew}>{trade.date}</Badge>
                        <span>{trade.isActive}</span>
                    </td>
                    {/*<div>{trade.dongName}</div>*/}
                    <td className={cx('p-2')}
                        onClick={() => {
                            history.push('/b/' + trade.buildingId);
                        }}>
                        <p className={cx('w-22 whitespace-nowrap overflow-hidden overflow-ellipsis')}>
                            {trade.name}
                        </p>
                    </td>
                    <td className={cx('p-2')}>
                        <p className={cx('truncate')}>
                            {trade.area.type}
                        </p>
                    </td>
                    <td className={cx('p-2')}>
                        <p className={cx('truncate')}>
                            {trade.floor}층
                        </p>
                    </td>
                    {/*<td className={cx('w-1/12 p-2')}>{trade.isRent ? '있음' : '-'}</td>*/}
                    <td className={cx('p-2')}>
                        <p className={cx('truncate')}>
                            {trade.beforeMaxPrice == 0 ? '-' : trade.beforeMaxPriceName}
                        </p>
                    </td>
                    <td className={cx('p-2')}>
                        <p className={cx('truncate font-bold', trade.isHighPrice && 'text-red-600', trade.minusPrice < 0 && 'text-gray-400')}>
                            {trade.priceName}{trade.subPrice > 0 && '/' + trade.subPrice}
                        </p>
                    </td>
                </tr>
            );
        };

        const table = () => {
            return (
                <table className={cx('w-full')}>
                    <thead>
                    <tr className={cx('bg-gray-50 border-t border-b text-gray-500')}>
                        <th className={cx('p-2 font-normal')}>
                            날짜
                        </th>
                        <th className={cx('p-2 font-normal')}>
                            이름
                        </th>
                        <th className={cx('p-2 font-normal')}>
                            타입
                        </th>
                        <th className={cx('p-2 font-normal')}>
                            층
                        </th>
                        {/*<th className={cx('w-1/12 p-2 font-normal')}>*/}
                        {/*    세*/}
                        {/*</th>*/}
                        <th className={cx('p-2 font-normal')}>
                            전고가
                        </th>
                        <th className={cx('p-2 font-normal')}>
                            가격
                        </th>
                    </tr>
                    </thead>
                    <tbody>
                    {
                        tradeList.map((trade, idx) => getRow(page, idx, trade))
                    }
                    </tbody>
                </table>
            )
        };

        const empty = () => {
            return (
                tradeList.length === 0 && isCompleted &&
                <div className={cx('empty_result')}>검색결과가 없습니다</div>
            )
        };

        return (
            <div className={cx('list')}>
                {table()}
                {empty()}
                {
                    !isCompleted ?
                        <div className={cx('h-16 p-3 flex justify-center border-b')} onClick={() => {
                            if (page === 1) {
                                setIsPreview(false)
                            }
                            fetch();
                        }}>
                            {
                                (
                                    isLoading ?
                                        <div
                                            className={cx('flex justify-center items-center w-full p-2 rounded text-gray-500 border bg-green-600')}>
                                            <Loading isLoading={isLoading}/>
                                        </div> :
                                        <div
                                            className={cx('flex justify-center items-center w-full p-2 rounded text-gray-500 border bg-white')}>
                                            더보기
                                            <svg className="w-3 h-3 ml-1" fill="none" stroke="currentColor"
                                                 viewBox="0 0 24 24"
                                                 xmlns="http://www.w3.org/2000/svg">
                                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                                      d="M19 9l-7 7-7-7"/>
                                            </svg>
                                        </div>
                                )
                            }
                        </div> :
                        <div className={cx('pb-5')}/>
                }
            </div>
        );
    }
;

export default MarketList;