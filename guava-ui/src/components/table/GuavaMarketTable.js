import React, {useEffect, useState} from 'react'
import {useHistory} from 'react-router-dom';
import {useRecoilState, useRecoilValue} from 'recoil';
import InfiniteScroll from 'react-infinite-scroll-component';

import {areaTypeState, filterAreaState, regionState, tradeDateState, tradeTypeState} from '../datatool/state';
import {getRegionTradeMarket, getTradeMarket} from '../datatool/api';

import classNames from 'classnames/bind';
import styles from './guavaTable.module.scss';
import {getEndArea, getStartArea} from '../constant';
import GuavaMarketRow from './row/GuavaMarketRow';
import GuavaRegionMarketRow from './row/GuavaRegionMarketRow';
import GuavaRegionMarketCol from './col/GuavaRegionMarketCol';
import GuavaMarketCol from './col/GuavaMarketCol';
import GuavaLoading from '../detail/GuavaLoading';

const cx = classNames.bind(styles);

const GuavaMarketTable = () => {
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

    useEffect(() => {
        if (tradeList.length === 0) {
            fetch();
        }
    }, [tradeList]);

    useEffect(() => {
        setPage(0);
        setTradeList([]);
        setIsLoading(false);
        setIsCompleted(false);
        // fetch();
    }, [region, tradeType, areaType, filterArea, tradeDate]);

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
                result = await getTradeMarket(tradeType, region.buildingId, page, areaType.areaId, date);
            } else {
                result = await getRegionTradeMarket(tradeType, region.id, page, startArea, endArea, date);
            }

            if (result.length === 0) {
                setIsCompleted(true);
            }

            let data = tradeList.concat(result.map((trade, idx) => getRow(page, idx, trade)));
            setTradeList(data);
            setPage(page + 1);
            setIsLoading(false);
        }
    };

    const getRow = (page, index, trade) => {
        if (region.type === 'BUILDING') {
            return <GuavaMarketRow page={page} idx={index} trade={trade}/>;
        } else {
            return <GuavaRegionMarketRow page={page} idx={index} trade={trade}/>;
        }
    };

    const getCol = () => {
        if (region.type === 'BUILDING') {
            return <GuavaMarketCol/>;
        } else {
            return <GuavaRegionMarketCol/>;
        }
    };

    const table = () => {
        return (
            <div className={cx('body')}>
                <InfiniteScroll
                    dataLength={tradeList.length} //This is important field to render the next data
                    next={fetch}
                    hasMore={!isCompleted}
                    loader={<GuavaLoading isLoading={isLoading}/>}
                    /*endMessage={tradeList.length > 0 &&
                    <div className={cx('empty_result')}>총 검색결과 {tradeList.length}개</div>}*/
                >
                    {tradeList}
                </InfiniteScroll>
                {
                    tradeList.length > 0 &&
                    <div className={cx('empty_result')}>총 검색결과 {tradeList.length}개</div>
                }
            </div>
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
            {getCol()}
            {table()}
            {empty()}
        </div>
    );
};

export default GuavaMarketTable;