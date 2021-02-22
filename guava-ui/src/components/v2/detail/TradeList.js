import React, {useEffect, useState} from 'react'
import {useHistory} from 'react-router-dom';
import {useRecoilState, useRecoilValue} from 'recoil';

import {areaTypeState, filterAreaState, regionState, tradeDateState, tradeTypeState} from '../../datatool/state';
import {getRegionTrade, getTrade} from '../../datatool/api';

import classNames from 'classnames/bind';
import styles from './tradeList.module.scss';
import {getEndArea, getStartArea} from '../../constant';
import Loading from './Loading';
import ArrowLeftOutlined from '@ant-design/icons/es/icons/ArrowLeftOutlined';
import {Badge} from "antd-mobile";

const cx = classNames.bind(styles);

const INIT_ITEM_SIZE = 5;
const ITEM_SIZE = 30;

const TradeList = () => {
        const history = useHistory();
        const [page, setPage] = useState(0);
        const [isLoading, setIsLoading] = useState(false);
        const [isCompleted, setIsCompleted] = useState(false);
        const [tradeList, setTradeList] = useState([]);
        const [areaType, setAreaType] = useRecoilState(areaTypeState);
        const [baseDate, setBaseDate] = useRecoilState(tradeDateState);
        const [tradeDate, setTradeDate] = useState(baseDate);
        const [filterArea, setFilterArea] = useRecoilState(filterAreaState);
        const region = useRecoilValue(regionState);
        const [count, setCount] = useState(0);
        const [tradeType, setTradeType] = useRecoilState(tradeTypeState);

        useEffect(() => {
            initFetch();
        }, []);

        // useEffect(() => {
        //     if (tradeList.length > 0) {
        //         fetch();
        //     }
        // }, [tradeList]);

        useEffect(() => {
            setTradeDate(baseDate);
        }, [baseDate]);

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
                    result = await getTrade(tradeType, region.buildingId, page, INIT_ITEM_SIZE, areaType.areaId, date);
                } else {
                    result = await getRegionTrade(tradeType, region.id, page, INIT_ITEM_SIZE, startArea, endArea, date);
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
                    result = await getTrade(tradeType, region.buildingId, page, ITEM_SIZE, areaType.areaId, date);
                } else {
                    result = await getRegionTrade(tradeType, region.id, page, ITEM_SIZE, startArea, endArea, date);
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
                <tr groupKey={page} key={page + '-' + index} className={cx('border-b text-gray-600 text-center text-xs')}>
                    <td className={cx('p-2')}>
                        <Badge dot={trade.isNew}>{trade.date}</Badge>
                    </td>
                    {/*<div>{trade.dongName}</div>*/}
                    <td className={cx('p-2')}
                        onClick={() => {
                            history.push('/b/' + trade.buildingId);
                        }}>
                        <p className={cx('w-20 whitespace-nowrap overflow-hidden overflow-ellipsis')}>
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
                    <td className={cx('p-2')}>
                        <p className={cx('truncate font-bold', trade.isHighPrice && 'text-red-600')}>
                            {trade.price}{trade.subPrice > 0 && '/' + trade.subPrice}
                        </p>
                    </td>
                </tr>
            );
        };

        return (
            <>
                {
                    tradeDate &&
                    <div className={cx('flex items-center p-5')}>
                        <div onClick={() => {
                            setTradeDate(null);
                            setCount(0);
                            // fetchChart();
                        }}
                             className={cx('flex items-center flex-none text-center text-black transform hover:scale-110 motion-reduce:transform-none cursor-pointer')}>
                            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"
                                 xmlns="http://www.w3.org/2000/svg">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                      d="M10 19l-7-7m0 0l7-7m-7 7h18"/>
                            </svg>
                        </div>
                        <span className={cx('ml-2 text-gray-700 font-bold text-lg')}>{tradeDate.format('YYYY년 M월')}</span>
                        <span className={cx('ml-1 text-gray-400 font-medium text-sm')}>({tradeList.length}건)</span>
                    </div>
                }
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
                <div
                    className={cx('flex justify-center items-center w-full')}>
                    <Loading isLoading={isLoading}/>
                </div>
                {
                    (!isCompleted) ?
                        <div className={cx('h-16 p-3 flex justify-center border-b')} onClick={() => {
                            fetch();
                        }}>
                            <div
                                className={cx('flex justify-center items-center w-full p-2 rounded text-gray-500 border bg-white')}>
                                더보기
                                <svg className="w-3 h-3 ml-1" fill="none" stroke="currentColor" viewBox="0 0 24 24"
                                     xmlns="http://www.w3.org/2000/svg">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                          d="M19 9l-7 7-7-7"/>
                                </svg>
                            </div>
                        </div> :
                        <div className={cx('flex justify-center p-5 text-gray-400')}>
                            <p>마지막 검색 결과입니다</p>
                        </div>
                }
            </>
        );
    }
;

export default TradeList;