import React, {useEffect, useState} from 'react'
import {useHistory} from 'react-router-dom';
import {useRecoilState, useRecoilValue} from 'recoil';
import InfiniteScroll from 'react-infinite-scroll-component';

import {areaTypeState, filterAreaState, regionState, tableOptionState, tradeDateState} from '../datatool/state';
import {getRegionTrade, getTrade} from '../datatool/api';

import classNames from 'classnames/bind';
import styles from './guavaTable.module.scss';
import {getEndArea, getStartArea, TABLE_OPTION} from '../constant';
import GuavaTradeRow from './row/GuavaTradeRow';
import GuavaMarketRow from './row/GuavaMarketRow';
import GuavaRegionTradeRow from './row/GuavaRegionTradeRow';
import GuavaRegionMarketRow from './row/GuavaRegionMarketRow';
import GuavaTradeCol from './col/GuavaTradeCol';
import GuavaRegionMarketCol from './col/GuavaRegionMarketCol';
import GuavaRegionTradeCol from './col/GuavaRegionTradeCol';
import GuavaMarketCol from './col/GuavaMarketCol';
import GuavaLoading from '../detail/GuavaLoading';
import ArrowLeftOutlined from '@ant-design/icons/es/icons/ArrowLeftOutlined';

const cx = classNames.bind(styles);

const GuavaTable = () => {
    const history = useHistory();
    const [page, setPage] = useState(0);
    const [isLoading, setIsLoading] = useState(false);
    const [isCompleted, setIsCompleted] = useState(false);
    const [tradeList, setTradeList] = useState([]);
    const [areaType, setAreaType] = useRecoilState(areaTypeState);
    const [tradeDate, setTradeDate] = useRecoilState(tradeDateState);
    // const [region, setRegion] = useRecoilState(regionState);
    const [tableOption, setTableOption] = useRecoilState(tableOptionState);
    const [filterArea, setFilterArea] = useRecoilState(filterAreaState);
    const region = useRecoilValue(regionState);
    const [count, setCount] = useState(0);
    const [chartList, setChartList] = useState([]);

    useEffect(() => {
        if (page === 0) {
            fetch();
        }
    }, [page]);

    useEffect(() => {
        setPage(0);
        setTradeList([]);
        setIsLoading(false);
        setIsCompleted(false);
        // fetch();
    }, [region, tableOption, areaType, filterArea, tradeDate]);

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
                result = await getTrade(tableOption, region.buildingId, page, areaType.areaId, date);
            } else {
                result = await getRegionTrade(tableOption, region.id, page, startArea, endArea, date);
            }

            if (result.length < 50) {
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
            if (tableOption === TABLE_OPTION.TRADE) {
                return <GuavaTradeRow page={page} idx={index} trade={trade}/>;
            } else if (tableOption === TABLE_OPTION.MARKET) {
                return <GuavaMarketRow page={page} idx={index} trade={trade}/>;
            }
        } else {
            if (tableOption === TABLE_OPTION.TRADE) {
                return <GuavaRegionTradeRow page={page} idx={index} trade={trade}/>;
            } else if (tableOption === TABLE_OPTION.MARKET) {
                return <GuavaRegionMarketRow page={page} idx={index} trade={trade}/>;
            }
        }
        return <></>;
    };

    const getCol = () => {
        if (region.type === 'BUILDING') {
            if (tableOption === TABLE_OPTION.TRADE) {
                return <GuavaTradeCol/>;
            } else if (tableOption === TABLE_OPTION.MARKET) {
                return <GuavaMarketCol/>;
            }
        } else {
            if (tableOption === TABLE_OPTION.TRADE) {
                return <GuavaRegionTradeCol/>;
            } else if (tableOption === TABLE_OPTION.MARKET) {
                return <GuavaRegionMarketCol/>;
            }
        }
    };

    const table = () => {
        return (
            <>
                <div className={cx('body')}>
                    <InfiniteScroll
                        dataLength={tradeList.length} //This is important field to render the next data
                        next={fetch}
                        hasMore={!isCompleted}
                        loader={<GuavaLoading isLoading={isLoading}/>}
                        endMessage={tradeList.length > 0 &&
                        <div className={cx('empty_result')}>총 검색결과 {tradeList.length}개</div>}
                        // below props only if you need pull down functionality
                        // // refreshFunction={fetch}
                        // // pullDownToRefresh
                        // // pullDownToRefreshThreshold={50}
                        // // pullDownToRefreshContent={
                        // //     <h3 style={{textAlign: 'center'}}>&#8595; Pull down to refresh</h3>
                        // // }
                        // releaseToRefreshContent={
                        //     <h3 style={{textAlign: 'center'}}>&#8593; Release to refresh</h3>
                        // }
                    >
                        {tradeList}
                    </InfiniteScroll>
                </div>
            </>
        )
    };

    const empty = () => {
        return (
            tradeList.length === 0 && isCompleted &&
            <div className={cx('empty_result')}>검색결과가 없습니다</div>
        )
    };

    return (
        <>
            {
                (TABLE_OPTION.TRADE === tableOption && tradeDate) &&
                <div className={cx('date')}>
                    <div className={cx('back_btn')} onClick={() => {
                        setChartList([]);
                        setTradeDate(null);
                        setCount(0);
                        // fetchChart();
                    }}>
                        <ArrowLeftOutlined/>
                    </div>
                    <span>{tradeDate.format('YYYY년 M월')}</span>
                    <span className={cx('count')}>({tradeList.length}건)</span>
                </div>
            }
            <div className={cx('list')}>
                {getCol()}
                {table()}
                {empty()}
            </div>
        </>
    );
};

export default GuavaTable;